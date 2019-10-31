<template>
	<div>
		<!-- <el-drawer
			title="测试"
			placement="right"
			:width="600"
			:visible="testVisible"
			@close="onClose">
			
			<div>当前的flowData:</div>
			<json-view 
				:value="flowData"
				:expand-depth=3
				boxed
				copyable/>
			
			<div style="margin-top: 12px;">暂存:</div>
			<el-textarea :autosize="{ minRows: 10, maxRows: 100 }" :value="flowDataJson" @change="editFlowDataJson" />
			
			<el-divider />
			<el-button @click="tempSave" :style="{ marginRight: '8px' }">暂存</el-button>
			<el-button @click="onLoad" type="primary">加载(暂存中的json数据)</el-button>
		</el-drawer> -->
	</div>
</template>

<script>
	import JsonView from 'vue-json-viewer'
	import { flowConfig } from '../config/args-config.js'
	
	export default {
		components: {
			JsonView
		},
		data () {
			return {
				testVisible: false,
				flowData: null,
				flowDataJson: ''
			}
		},
		methods: {
			onClose () {
				this.testVisible = false;
			},
			editFlowDataJson (e) {
				this.flowDataJson = e.target.value;
			},
			tempSave () {
				let tempObj = Object.assign({}, this.flowData);
				tempObj.status = flowConfig.flowStatus.SAVE;
				this.flowDataJson = JSON.stringify(tempObj);
			},
			onLoad () {
				this.$emit('loadFlow', this.flowDataJson);
				this.onClose();
			}
		}
	}
</script>

<style>
</style>