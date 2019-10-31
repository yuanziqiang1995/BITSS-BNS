<template>
	<div>
		<el-tabs size="small" defaultActiveKey="flow-attr" v-model="activeKey">
			<!--el-tab-pane key="flow-attr">
				<span slot="tab">
					<el-icon type="cluster" />
					流程属性
				</span>
				<el-form layout="horizontal">
					<el-form-item label="流程id" :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
						<el-input :value="flowData.attr.id" disabled />
					</el-form-item>
				</el-form>
			</el-tab-pane-->
			<el-tab-pane name="node-attr" label="节点属性">
				<!-- <span slot="tab">
					<i class="el-icon-edit"></i>
					节点属性
				</span>	 -->
				<span slot="label"><i class="el-icon-edit"></i> 节点属性</span>
				<el-form size="mini" label-position="left" label-width="70px">
					<!--el-form-item label="类型" :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
						<el-tag color="purple">{{ currentSelect.type }}</el-tag>
					</el-form-item-->
					<el-form-item label="id">
						<el-input v-model="currentSelect.id" disabled />
					</el-form-item>
					<el-form-item label="名称">
						<el-input placeholder="请输入节点名称" v-model="currentSelect.nodeName" @change="nodeNameChange" />
					</el-form-item>
					<el-form-item label="数据类型">
						<el-select placeholder="数据类型" v-model="defaultType"
							@change="handleDeliverChange" 
							:filterOption="filterDeliverOption">
							<el-option v-for="val in TypeData" :key="val.id" :value="val.id" :label="val.name">
								{{val.name}}
							</el-option>
						</el-select>
					</el-form-item>
					<div v-if="showValueType">
						<el-form-item  label="取值个数">
							<el-input placeholder="请输入取值个数" v-model="currentSelect.valueNum" @change="valueNumChange" />
						</el-form-item>
						<el-form-item  label="CPT">
							<el-input type="textarea" :rows="4" v-model="currentSelect.CPT" @change="CPTChange"/>
						</el-form-item>
						
						<!-- <el-form-item  :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
							<div v-for="(node, index) in currentParents" v-bind:key="node.id" :value="node.id">
								<el-divider dashed>{{node.nodeName}}</el-divider>	
								<div v-for="n in node.valueNum" :key="n" :value="n">
									<el-input :value="currentSelect.CPT[index][n]" @change="CPTChange" />
								</div>
							</div>
						</el-form-item> -->
					</div>
				</el-form>
				
			</el-tab-pane>
			<el-tab-pane name="link-attr" label="连线属性">
				<span slot="label"><i class="el-icon-share"></i> 节点属性</span>	
				<el-form size="mini" label-position="left" label-width="70px">
					<el-form-item label="id" :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
						<el-input v-model="currentSelect.id" disabled />
					</el-form-item>
					<el-form-item label="源节点" :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
						<el-input v-model="currentSelect.sourceId" disabled />
					</el-form-item>
					<el-form-item label="目标节点" :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
						<el-input v-model="currentSelect.targetId" disabled />
					</el-form-item>
					<el-form-item label="文本" :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
						<el-input v-model="currentSelect.label" @change="linkLabelChange" />
					</el-form-item>
				</el-form>
			</el-tab-pane>
		</el-tabs>
	</div>
</template>

<script>
	import jsplumb from 'jsplumb'
	import { flowConfig } from '../config/args-config.js'
	
	export default {
		props: ['plumb', 'flowData', 'select'],
		components: {
			jsplumb
		},
		data () {
			return {
				currentSelect: this.select,
				formItemLayout: {
					labelCol: { span: 6 },
					wrapperCol: { span: 16 }
				},
				activeKey: 'flow-attr',
				TypeData: [{
						id: '0',
						name: '离散型'
					},
					{
						id: '1',
						name: '连续型'
					},
				],
				defaultType: '0',
				showValueType: true,
				
			}
		},
		methods: {
			nodeNameChange (e) {
				this.currentSelect.nodeName = e.target.value;
			},
			valueNumChange (e) {
				// this.currentSelect.valueNum = parseInt(e.target.value);
				// console.log(typeof this.currentSelect.valueNum)
				if (this.currentSelect.valueNum) {
					this.$nextTick(() => {
					this.currentSelect.valueNum = parseInt(this.currentSelect.valueNum);
					})
				}
			},
			CPTChange (e) {
				this.currentSelect.CPT = e.target.value;
			},
			linkLabelChange (e) {
				const that = this;
				let label = e.target.value;
				
				that.currentSelect.label = label;
				let conn = that.plumb.getConnections({
					source: that.currentSelect.sourceId,
					target: that.currentSelect.targetId
				})[0];
				if (label != '') {
					conn.setLabel({
						label: label,
						cssClass: 'linkLabel'
					});
				} else {
					let labelOverlay = conn.getLabelOverlay();
					if (labelOverlay) conn.removeOverlay(labelOverlay.id);
				}
			},
			filterDeliverOption(input, option) {
          		return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
			 },
			handleDeliverChange(value) {
				this.currentSelect.nodeType = value;  
				//console.log(this.currentSelect.nodeType);
				if(value == '0'){
					this.showValueType = true;
				}
				else{
					this.showValueType = false;
				}
 			}
		},
		watch: {
			select (val) {
				this.currentSelect = val;
				if (this.currentSelect.type == 'link') {
					this.activeKey = 'link-attr';
				} else if (!this.currentSelect.type) {
					this.activeKey = 'flow-attr';
				} else {
					this.activeKey = 'node-attr';
				}
			},
			currentSelect: {
				handler (val) {
					this.$emit('update:select', val);
				},
				deep: true
			}
		}
	}
</script>

<style lang="scss">
	@import '../style/flow-attr.scss'
</style>