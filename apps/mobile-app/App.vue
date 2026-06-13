<script>
export default {
	onLaunch: function() {
		 const systemInfo = uni.getSystemInfoSync();
		this.statusBarHeight = systemInfo.statusBarHeight;
	},
	data() {
		return {
			statusBarHeight: 0
		};
	},
	globalData:{
		baseUrl:"http://your-server-host:8203/system"
	},
	onShow: function() {
		console.log('App Show');
	},
	onHide: function() {
		console.log('App Hide');
	}
};
</script>

<style>
/*每个页面公共css */ 
@import "colorui/main.css";
	@import "colorui/icon.css";
body {
	background-color: #f5f8ff;
	background-image:
		radial-gradient(circle at 0% 0%, rgba(59, 140, 255, 0.25) 0, transparent 42%),
		radial-gradient(circle at 100% 100%, rgba(31, 95, 202, 0.15) 0, transparent 44%);
  padding-top: env(safe-area-inset-top);
   padding-bottom: env(safe-area-inset-bottom);
   box-sizing: border-box;
	color: #103268;
}
</style>
