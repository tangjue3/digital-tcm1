package com.ruoyi.system.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.TcmAppUserProfile;
import com.ruoyi.system.domain.chat.TcmChatConversation;
import com.ruoyi.system.domain.chat.TcmChatConversationSummary;
import com.ruoyi.system.domain.chat.TcmChatMessage;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.service.ITcmAppUserProfileService;
import com.ruoyi.system.service.ITcmMedicalChatService;

@Service
public class TcmMedicalChatServiceImpl implements ITcmMedicalChatService
{
    private final ObjectMapper objectMapper;

    @Autowired
    private ITcmAppUserProfileService appUserProfileService;

    @Autowired
    private ISysUserService sysUserService;

    public TcmMedicalChatServiceImpl()
    {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public TcmChatConversation getAppConversation(Long patientUserId)
    {
        return enrichConversation(loadConversation(patientUserId));
    }

    @Override
    public TcmChatMessage sendAppMessage(Long patientUserId, SysUser patientUser, String content)
    {
        TcmChatConversation conversation = loadConversation(patientUserId);
        applyPatientInfo(conversation, patientUserId, patientUser);
        if (StringUtils.isEmpty(conversation.getDoctorName()))
        {
            conversation.setDoctorName("接诊医生");
        }
        TcmChatMessage message = buildMessage("patient", patientUser, content);
        conversation.getMessages().add(message);
        conversation.setUpdatedTime(message.getSendTime());
        saveConversation(conversation);
        return message;
    }

    @Override
    public List<TcmChatConversationSummary> listAdminConversations()
    {
        List<TcmChatConversationSummary> summaries = new ArrayList<>();
        for (TcmChatConversation conversation : loadAllConversations())
        {
            TcmChatConversation enriched = enrichConversation(conversation);
            TcmChatConversationSummary summary = new TcmChatConversationSummary();
            summary.setPatientUserId(enriched.getPatientUserId());
            summary.setPatientName(enriched.getPatientName());
            summary.setPatientPhone(enriched.getPatientPhone());
            summary.setDoctorName(enriched.getDoctorName());
            summary.setMessageCount(enriched.getMessages().size());
            if (!enriched.getMessages().isEmpty())
            {
                TcmChatMessage lastMessage = enriched.getMessages().get(enriched.getMessages().size() - 1);
                summary.setLastMessage(lastMessage.getContent());
                summary.setLastMessageTime(lastMessage.getSendTime());
            }
            summaries.add(summary);
        }
        summaries.sort(Comparator.comparing(TcmChatConversationSummary::getLastMessageTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return summaries;
    }

    @Override
    public TcmChatConversation getAdminConversation(Long patientUserId, SysUser doctorUser)
    {
        TcmChatConversation conversation = loadConversation(patientUserId);
        applyDoctorInfo(conversation, doctorUser);
        saveConversation(conversation);
        return enrichConversation(conversation);
    }

    @Override
    public TcmChatMessage sendAdminMessage(Long patientUserId, SysUser doctorUser, String content)
    {
        TcmChatConversation conversation = loadConversation(patientUserId);
        applyDoctorInfo(conversation, doctorUser);
        applyPatientInfo(conversation, patientUserId, null);
        TcmChatMessage message = buildMessage("doctor", doctorUser, content);
        conversation.getMessages().add(message);
        conversation.setUpdatedTime(message.getSendTime());
        saveConversation(conversation);
        return message;
    }

    private TcmChatMessage buildMessage(String senderRole, SysUser senderUser, String content)
    {
        String cleanContent = StringUtils.trim(content);
        if (StringUtils.isEmpty(cleanContent))
        {
            throw new ServiceException("消息内容不能为空");
        }
        TcmChatMessage message = new TcmChatMessage();
        message.setMessageId(UUID.randomUUID().toString().replace("-", ""));
        message.setSenderRole(senderRole);
        message.setSenderUserId(senderUser == null ? null : senderUser.getUserId());
        message.setSenderName(resolveUserDisplayName(senderUser));
        message.setContent(cleanContent);
        message.setSendTime(new Date());
        return message;
    }

    private TcmChatConversation enrichConversation(TcmChatConversation conversation)
    {
        if (conversation == null)
        {
            return new TcmChatConversation();
        }
        applyPatientInfo(conversation, conversation.getPatientUserId(), null);
        if (StringUtils.isEmpty(conversation.getDoctorName()))
        {
            conversation.setDoctorName("接诊医生");
        }
        if (conversation.getMessages() == null)
        {
            conversation.setMessages(new ArrayList<>());
        }
        return conversation;
    }

    private void applyPatientInfo(TcmChatConversation conversation, Long patientUserId, SysUser currentUser)
    {
        conversation.setPatientUserId(patientUserId);
        SysUser patientUser = currentUser;
        if (patientUser == null && patientUserId != null)
        {
            patientUser = sysUserService.selectUserById(patientUserId);
        }
        TcmAppUserProfile profile = patientUserId == null ? null : appUserProfileService.selectAppUserProfileByUserId(patientUserId);
        conversation.setPatientName(firstNotBlank(
                profile == null ? null : profile.getNickName(),
                patientUser == null ? null : patientUser.getNickName(),
                patientUser == null ? null : patientUser.getUserName(),
                conversation.getPatientName(),
                patientUserId == null ? "患者" : "患者" + patientUserId));
        conversation.setPatientPhone(firstNotBlank(
                profile == null ? null : profile.getPhonenumber(),
                patientUser == null ? null : patientUser.getPhonenumber(),
                conversation.getPatientPhone(),
                ""));
    }

    private void applyDoctorInfo(TcmChatConversation conversation, SysUser doctorUser)
    {
        if (doctorUser == null)
        {
            return;
        }
        conversation.setDoctorUserId(doctorUser.getUserId());
        conversation.setDoctorName(firstNotBlank(
                doctorUser.getNickName(),
                doctorUser.getUserName(),
                conversation.getDoctorName(),
                "接诊医生"));
    }

    private String resolveUserDisplayName(SysUser user)
    {
        if (user == null)
        {
            return "";
        }
        return firstNotBlank(user.getNickName(), user.getUserName(), "用户");
    }

    private String firstNotBlank(String... values)
    {
        if (values == null)
        {
            return "";
        }
        for (String value : values)
        {
            if (StringUtils.isNotEmpty(value))
            {
                return value;
            }
        }
        return "";
    }

    private List<TcmChatConversation> loadAllConversations()
    {
        Path directory = getStorageDirectory();
        if (!Files.exists(directory))
        {
            return new ArrayList<>();
        }
        try (Stream<Path> stream = Files.list(directory))
        {
            return stream
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .map(this::readConversationFile)
                    .filter(conversation -> conversation != null && conversation.getPatientUserId() != null)
                    .sorted(Comparator.comparing(TcmChatConversation::getUpdatedTime,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .collect(Collectors.toList());
        }
        catch (IOException e)
        {
            throw new ServiceException("读取聊天记录失败");
        }
    }

    private synchronized TcmChatConversation loadConversation(Long patientUserId)
    {
        if (patientUserId == null)
        {
            throw new ServiceException("患者信息不存在");
        }
        Path path = getConversationPath(patientUserId);
        if (!Files.exists(path))
        {
            TcmChatConversation conversation = new TcmChatConversation();
            conversation.setPatientUserId(patientUserId);
            conversation.setUpdatedTime(null);
            return conversation;
        }
        TcmChatConversation conversation = readConversationFile(path);
        if (conversation == null)
        {
            conversation = new TcmChatConversation();
            conversation.setPatientUserId(patientUserId);
        }
        if (conversation.getMessages() == null)
        {
            conversation.setMessages(new ArrayList<>());
        }
        return conversation;
    }

    private synchronized void saveConversation(TcmChatConversation conversation)
    {
        Path path = getConversationPath(conversation.getPatientUserId());
        try
        {
            Files.createDirectories(path.getParent());
            try (OutputStream outputStream = Files.newOutputStream(path,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE))
            {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputStream, conversation);
            }
        }
        catch (IOException e)
        {
            throw new ServiceException("保存聊天记录失败");
        }
    }

    private TcmChatConversation readConversationFile(Path path)
    {
        try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ))
        {
            return objectMapper.readValue(inputStream, TcmChatConversation.class);
        }
        catch (IOException e)
        {
            throw new ServiceException("读取聊天记录失败");
        }
    }

    private Path getConversationPath(Long patientUserId)
    {
        return getStorageDirectory().resolve("patient-" + patientUserId + ".json");
    }

    private Path getStorageDirectory()
    {
        String userDir = System.getProperty("user.dir", ".");
        return Paths.get(userDir, ".medical-chat");
    }
}
