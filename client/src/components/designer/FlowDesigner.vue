<template>

		<el-container class="container">
			<el-aside
				width="260px"
				theme="light"
				class="select-area">
				<el-row>
					<el-tag v-model="tag.checked0" @change="toggleNodeShow0" class="tag">工具</el-tag>
					<div align="center">
						<el-button-group style="margin:10px">
							<el-button 
								v-for="(tool, index) in field.tools" 
								:key="index" 
								:icon="tool.icon"
								type="mini"
								@click="selectTool(tool.type)">
							</el-button>
						</el-button-group>

					</div>
				</el-row>
				<el-row>
					<el-tag v-model="tag.checked1" @change="toggleNodeShow1" class="tag">添加节点</el-tag>
					<div align="center">
						<!-- <el-row
							:grid="{ gutter: 8, column: 2 }"
							v-if="tag.commonNodeShow">
							<el-list-item v-for="(commonNode, index) in field.commonNodes" :key="index">
								<div class="node-item" :type="commonNode.type" belongto="commonNodes">
									<el-icon :component="commonNode.icon" /> {{ commonNode.nodeName }}
								</div>
							</el-list-item>
						</el-row> -->				
						<div class="node-item" type="common" belongto="commonNodes">
							<el-icon :component="el-icon-document" /> 拖拽添加节点
						</div>
					</div>
				</el-row>
				<el-row>
					<flow-attr :plumb="plumb" :flowData="flowData" :select.sync="currentSelect"></flow-attr>
				</el-row>
			
			</el-aside >
			<el-container>
				<el-header class="header-option">
					<!-- <el-tooltip content="保存网络" placement="bottom">
						<el-button @click="createBN" class="header-option-button" icon="el-icon-document"></el-button>
					</el-tooltip> -->
					
					<el-button @click="createBN" type="primary">保存网络</el-button>
					
					<!-- <el-tooltip content="生成网络图片" placement="bottom">
						<el-button @click="exportFlowPicture" class="header-option-button" icon="el-icon-picture-outline"></el-button>
					</el-tooltip>
					<el-popconfirm content="确认要重新绘制吗？" placement="bottom" okText="确认" cancelText="取消" @confirm="clear">
						<el-tooltip content="重新绘制" placement="bottom">
							<el-button class="header-option-button" icon="el-icon-delete"></el-button>
						</el-tooltip>
					</el-popconfirm>
					<el-tooltip :content="flowData.config.showGridText" placement="bottom">
						<el-button 
							@click="toggleShowGrid" 
							class="header-option-button" 
							size="small" 
							:icon="flowData.config.showGridIcon">
						</el-button>
						<el-button 
							@click="toggleShowGrid" 
							class="header-option-button" 
							icon="el-icon-view">
						</el-button>
					</el-tooltip> 
					<el-tooltip content="设置" placement="bottom">
						<el-button @click="setting" class="header-option-button" icon="el-icon-setting"></el-button>
					</el-tooltip>-->
				</el-header>
				<el-main class="content">
					<flow-area 
						ref="flowArea" 
						:browserType="browserType"
						:flowData="flowData" 
						:select.sync="currentSelect" s
						:selectGroup.sync="currentSelectGroup" 
						:parents.sync="currentParents"
						:plumb="plumb" 
						:currentTool="currentTool" 
						@findNodeConfig="findNodeConfig"
						@selectTool="selectTool"
						@getShortcut="getShortcut"
						@saveFlow="saveFlow">
					</flow-area>
					
					<!-- <el-form-item label="名称" :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
						<el-input :value="currentParents.id" disabled />
					</el-form-item> -->
					<vue-context-menu 
						:contextMenuData="linkContextMenuData" 
						@deleteLink="deleteLink">
					</vue-context-menu>
				</el-main>
				<el-footer class="foot">
				</el-footer>
			</el-container>
			<el-aside
				width="350px"
				theme="light"
				class="attr-area"
				@mousedown.stop="loseShortcut">
				<el-form style="margin-top: 60px; margin-left: 10px; margin-right: 10px" size="mini" label-position="left" label-width="70px">
					<el-form-item label="id">
						<el-input v-model="currentSelect.id" disabled />
					</el-form-item>
					<el-form-item label="名称">
						<el-input v-model="currentSelect.nodeName" disabled />
					</el-form-item>
					<div v-if="currentParents.length">
						<el-tag type="info">证据</el-tag>		
						<div v-for="node in currentParents" v-bind:key="node.id" :value="node.id" >
							<el-form-item :label="node.nodeName">
								<!-- <el-select  v-for="node in currentParents" v-bind:key="node.id" :value="node.id"
									showSearch
									placeholder="Select a person"
									optionFilterProp="children"
									style="width: 200px">
										<el-select-option v-for="node in currentParents" v-bind:key="node.id" :value="node.id">
											{{node.name}}
										</el-select-option>
								</el-select> -->
								<el-radio-group v-model="node.evidence">
									<el-radio v-for="n in node.valueNum" :key="n-1" :label="n-1">{{n-1}}</el-radio>
								</el-radio-group>
							</el-form-item>
						</div>
						<div align="center">
						<el-button  type="primary" @click="infer">查询</el-button>
						</div>
					</div>
				
				</el-form>
				<div v-if="showQueryResult">
					<el-card class="box-card">
					<div slot="header" class="clearfix">
						<span>查询结果</span>
						<el-button style="float: right; padding: 3px 0" type="text" @click="handleQueryResult">关闭</el-button>
					</div>
					<div class="text item">
						{{response}}
					</div>
					</el-card>
				</div>
				
			</el-aside>
		</el-container>
		<!-- <el-modal 
			:title="'网络图_' + flowData.attr.id + '.png'"
			centered
			width="90%"
			:closable="flowPicture.closable"
			:maskClosable="flowPicture.maskClosable"
			:visible="flowPicture.modalVisible"
			okText="下载到本地"
			cancelText="取消"
			@ok="downLoadFlowPicture"
			@cancel="cancelDownLoadFlowPicture">
			<div align="center">
				<img :src="flowPicture.url" />
			</div>
		</el-modal>
		<setting-modal ref="settingModal"></setting-modal>
		<shortcut-modal ref="shortcutModal"></shortcut-modal>
		<test-modal ref="testModal" @loadFlow="loadFlow"></test-modal> -->

</template>

<script>
	import jsplumb from 'jsplumb'
	import { tools, commonNodes } from './config/basic-node-config.js'
	import { flowConfig } from './config/args-config.js'
	import { startSvg, endSvg, commonSvg, freedomSvg, gatewaySvg, eventSvg, childFlowSvg, xLaneSvg, yLaneSvg, lanePoolSvg } from './config/basic-icon-config.js'
	import $ from 'jquery'
	import 'jquery-ui/ui/widgets/draggable'
	import 'jquery-ui/ui/widgets/droppable'
	import 'jquery-ui/ui/widgets/resizable'
	import html2canvas from 'html2canvas'
	import canvg from 'canvg'
	import { ZFSN } from './util/ZFSN.js'
	import FlowArea from './modules/FlowArea'
	import FlowAttr from './modules/FlowAttr'
	import SettingModal from './modules/SettingModal'
	import ShortcutModal from './modules/ShortcutModal'
	import UsingDocModal from './modules/UsingDocModal'
	import TestModal from './modules/TestModal'
	import { createNetwork, queryInference } from '@/api/bn'
	//import { CREATE } from './api/bn.js'

	export default {
		name: 'vfd',
		components: {
			jsplumb,
			flowConfig,
			html2canvas,
			canvg,
			FlowArea,
			FlowAttr,
			SettingModal,
			ShortcutModal,
			UsingDocModal,
			TestModal,
			StartIcon: { template: startSvg },
			EndIcon: { template: endSvg },
			CommonIcon: { template: commonSvg },
			FreedomIcon: { template: freedomSvg },
			GatewayIcon: { template: gatewaySvg },
			EventIcon: { template: eventSvg },
			ChildFlowIcon: { template: childFlowSvg },
			XLaneIcon: { template: xLaneSvg },
			YLaneIcon: { template: yLaneSvg },
			LanePoolIcon: { template: lanePoolSvg }
		},
		mounted () {
			const that = this;
			that.dealCompatibility();
			that.initNodeSelectArea();
			that.initJsPlumb();
			that.listenShortcut();
			that.initFlow();
			that.listenPage();
			
			setTimeout(function() {}, 1000);
		},
		data () {
			return {
				showQueryResult: false,
				queryResult: '',
				info: {
				},
				formItemLayout: {
					labelCol: { span: 6 },
					wrapperCol: { span: 16 }
				},
				request: {
					id: '',
					nodeList: [],
					linkList: [],
				},
				response: '',
				queryList: {
					networkId: '',
					targetId: '',
					evidence: []
				},
				evi:{
					id: '',
					value: {}
				},
				link:{
					id: '',
					sourceId: '',
					targetId: '',
					label: ''
				},
				node:{
					id: '',
					nodeName: '',
					valueNum: '',
					CPT: ''
				},
				tag: {
					checked0: true,
					checked1: true,
					checked2: true,
					checked3: true,
					toolShow: true,
					commonNodeShow: true
				},
				checkboxGroup1: 'drag',
				browserType: 3,
				plumb: {},
				field: {
					tools: tools,
					commonNodes: commonNodes
				},
				flowData: {
					nodeList: [],
					linkList: [],
					attr: {
						id: ''
					},
					config: {
						showGrid: true,
						showGridText: '隐藏网格',
						showGridIcon: 'eye'
					},
					status: flowConfig.flowStatus.CREATE,
					remarks: []
				},
				currentTool: {
					type: 'drag',
					icon: 'drag',
					name: '拖拽'
				},
				currentSelect: {},
				currentSelectGroup: [],
				currentParents: [],
				activeShortcut: true,
				linkContextMenuData: flowConfig.contextMenu.link,
				flowPicture: {
					url: '',
					modalVisible: false,
					closable: false,
					maskClosable: false
				}
			}
		},
		methods: {
			handleQueryResult (){
				this.showQueryResult = false;
				this.response = '';
			},
			getParentNodes (node){
				const that = this;

				let link;
				let count=0;
				let parent = []
				for(let i=0; i<this.flowData.linkList.length; i++){
					if(this.flowData.linkList[i].targetId == node.id){
						parent.push(this.flowData.linkList[i].sourceId);
					}
				}				
				return parent;
			},
			infer () {
				this.queryList.evidence = [];
				this.queryList.targetId = this.currentSelect.id;
				this.queryList.networkId = this.networkId;
				
				//console.log(this.currentParents);
				for(let i=0; i<this.currentParents.length; i++){
					let node = this.currentParents[i];
					let newevi = new Object();
					newevi.id = node.id;
					newevi.value = node.evidence;
					this.queryList.evidence.push(newevi);
				}
				//this.request.id = '1';
				console.log(this.queryList);
				//let user_info = CREATE(this.queryList);
				queryInference(this.queryList).then(response => {
					this.$notify({
					title: '成功',
					message: response.data.msg,
					type: 'success',
					duration: 2000
					})
					this.response = response;
				})
				console.log(this.response);
				if(this.response.data.code == '40000'){
					this.$alert('输入的网络格式有误', '错误', {
						confirmButtonText: '确定',
					});
				}
				else{
					this.showQueryResult = true;
					this.queryResult = this.response.data.data;
				}
			},
			createBN () {
				
				var array = [];
				for (var i = 0; i < this.flowData.nodeList.length; i++) {
					if (array.indexOf(this.flowData.nodeList[i].nodeName) === -1) {
						array.push(this.flowData.nodeList[i].nodeName)
					}
				}
				//console.log(array);
				if (array.length != this.flowData.nodeList.length){
					this.$alert('节点名称不允许有重复', '错误', {
						confirmButtonText: '确定',
						// callback: action => {
						// 	this.$message({
						// 	type: 'info',
						// 	message: `action: ${ action }`
						// 	});
						// }
					});
				}
				else{
					this.request.id = '';
					this.request.nodeList = [];
					this.request.linkList = [];

					let str, arr, arr1;

					for(let i=0; i<this.flowData.nodeList.length; i++){
						let node = this.flowData.nodeList[i];
						let newnode = new Object();
						newnode.id = node.id;
						newnode.nodeName = node.nodeName;
						newnode.valueNum = node.valueNum;

						str = node.CPT;
						arr = str.split("\n");
						
						//console.log(arr);
						for(let i=0; i<arr.length; i++){
							arr1 = arr[i].split(',');
							//console.log(arr1);
					
							for (let j=0; j<arr1.length; j++){
								arr1[j] = parseFloat(arr1[j].toString());
							}
							arr[i] = arr1;
						}
						//console.log(arr);
						newnode.CPT = arr;						
						newnode.sequence = this.getParentNodes(node);

						this.request.nodeList.push(newnode);
					}
					for(let i=0; i<this.flowData.linkList.length; i++){
						let link = this.flowData.linkList[i];
						let newlink = new Object();
						newlink.id = link.id;
						newlink.sourceId = link.sourceId;
						newlink.targetId = link.targetId;
						newlink.label = link.label;
						this.request.linkList.push(newlink);
					}

					//this.request.id = '1';

					this.request.id = this.networkId;
					
					console.log(this.request);
					//let user_info = CREATE(this.request);
					//console.log(user_info);

					this.listLoading = true
					createNetwork(this.request).then(response => {
						this.$notify({
						title: '成功',
						message: response.data.msg,
						type: 'success',
						duration: 2000
						})
					})				
				}			
			},
			onChange (e) {
				this.currentSelect.valueNum = e.target.value;
			},
			getS4() {
  				return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
			},
			getUuid() {
				return (this.getS4() + this.getS4() + '-' + this.getS4() + '-' + this.getS4() + '-' + this.getS4() + '-' + this.getS4() + this.getS4() + this.getS4());
			},
			toggleNodeShow0 (flag) {
				if (!flag) {
					this.tag.toolShow = false;
				} else {
					this.tag.toolShow = true;
				}
			},
			toggleNodeShow1 (flag) {
				if (!flag) {
					this.tag.commonNodeShow = false;
				} else {
					this.tag.commonNodeShow = true;
				}
			},
			toggleNodeShow2 (flag) {
				if (!flag) {
					this.tag.highNodeShow = false;
				} else {
					this.tag.highNodeShow = true;
				}
			},
			toggleNodeShow3 (flag) {
				if (!flag) {
					this.tag.laneNodeShow = false;
				} else {
					this.tag.laneNodeShow = true;
				}
			},
			getBrowserType () {
				let userAgent = navigator.userAgent;
				let isOpera = userAgent.indexOf("Opera") > -1;
				if (isOpera) {
					return 1;
				}; 
				if (userAgent.indexOf("Firefox") > -1) {
					return 2;
				} 
				if (userAgent.indexOf("Chrome") > -1) {
					return 3;
				}
				if (userAgent.indexOf("Safari") > -1) {
					return 4;
				} 
				if (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera) {
					alert('IE浏览器支持性较差，推荐使用Firefox或Chrome');
					return 5;
				}
				if (userAgent.indexOf("Trident") > -1) {
					alert('Edge浏览器支持性较差，推荐使用Firefox或Chrome');
					return 6;
				};
			},
			dealCompatibility () {
				const that = this;
				
				that.browserType = that.getBrowserType();
				if (that.browserType == 2) {
					flowConfig.shortcut.scaleContainer = {
						code: 16,
						codeName: 'SHIFT(chrome下为ALT)',
						shortcutName: '画布缩放',
					};
				}
			},
			initJsPlumb () {
				const that = this;
				
				that.plumb = jsPlumb.getInstance(flowConfig.jsPlumbInsConfig);
				
				that.plumb.bind('beforeDrop', function(info) {
					let sourceId = info.sourceId;
					let targetId = info.targetId;
					
					if (sourceId == targetId) return false;
					let filter = that.flowData.linkList.filter(link => (link.sourceId == sourceId && link.targetId == targetId));
					if (filter.length > 0) {
						that.$message.error('同方向的两节点连线只能有一条！');
						return false;
					}
					return true;
					
				});

				
				that.plumb.bind('connection', function(conn, e) {
					let connObj = conn.connection.canvas;
					let o = {}, id, label;
					if (that.flowData.status == flowConfig.flowStatus.CREATE || that.flowData.status == flowConfig.flowStatus.MODIFY) {
						id = 'link-' + ZFSN.getId();
						label = '';
					} else if (that.flowData.status == flowConfig.flowStatus.LOADING) {
						let l = that.flowData.linkList[that.flowData.linkList.length - 1];
						id = l.id;
						label = l.label;
					}
					//console.log(this.currentParents);
					
					connObj.id = id;
					o.type = 'link';
					o.id = id;
					o.sourceId = conn.sourceId;
					o.targetId = conn.targetId;
					o.label = label;
					o.cls = {
						linkType: flowConfig.jsPlumbInsConfig.Connector[0],
						linkColor: flowConfig.jsPlumbInsConfig.PaintStyle.stroke,
						linkThickness: flowConfig.jsPlumbInsConfig.PaintStyle.strokeWidth
					};
					$('#' + id).bind('contextmenu', function(e) {
						that.showLinkContextMenu(e);
						that.currentSelect = that.flowData.linkList.filter(l => l.id == id)[0];
					});
					$('#' + id).bind('click', function(e) {
						let event = window.event || e;
						event.stopPropagation();
						that.currentSelect = that.flowData.linkList.filter(l => l.id == id)[0];
					});
					if (that.flowData.status != flowConfig.flowStatus.LOADING) that.flowData.linkList.push(o);
					
				});
				
				that.plumb.importDefaults({
					ConnectionsDetachable: flowConfig.jsPlumbConfig.conn.isDetachable
				});
				
				let id = this.getUuid();
				this.networkId = id;
				console.log(this.networkId);

				ZFSN.consoleLog(['实例化JsPlumb成功...']);
			},
			initNodeSelectArea () {
				$(document).ready(function() {
					$(".node-item").draggable({
						opacity: flowConfig.defaultStyle.dragOpacity,
						helper: 'clone',
						cursorAt: {
							top: 16,
							left: 60
						},
						containment: "window",
						revert: "invalid"
					});
					ZFSN.consoleLog(['初始化节点选择列表成功...']);
				});
			},
			listenShortcut () {
				// const that = this;
				// document.onkeydown = function(e) {
				// 	let event = window.event || e;
					
				// 	if (!that.activeShortcut) return;
				// 	let key = event.keyCode;
					
				// 	switch (key) {
				// 		case flowConfig.shortcut.multiple.code:
				// 			that.$refs.flowArea.rectangleMultiple.flag = true;
				// 			break;
				// 		case flowConfig.shortcut.dragContainer.code:
				// 			that.$refs.flowArea.container.dragFlag = true;
				// 			break;
				// 		case flowConfig.shortcut.scaleContainer.code:
				// 			that.$refs.flowArea.container.scaleFlag = true;
				// 			break;
				// 		case flowConfig.shortcut.dragTool.code: 
				// 			that.selectTool('drag');
				// 			break;
				// 		case flowConfig.shortcut.connTool.code:
				// 			that.selectTool('connection');
				// 			break;
				// 		case flowConfig.shortcut.zoomInTool.code:
				// 			that.selectTool('zoom-in');
				// 			break;
				// 		case flowConfig.shortcut.zoomOutTool.code:
				// 			that.selectTool('zoom-out');
				// 			break;
				// 		case 37:
				// 			that.moveNode('left');
				// 			break;
				// 		case 38:
				// 			that.moveNode('up');
				// 			break;
				// 		case 39:
				// 			that.moveNode('right');
				// 			break;
				// 		case 40:
				// 			that.moveNode('down');
				// 			break;
				// 	}
					
				// 	if (event.ctrlKey) {
				// 		if (event.altKey) {
				// 			switch (key) {
				// 				case flowConfig.shortcut.settingModal.code:
				// 					that.setting();
				// 					break;
				// 				case flowConfig.shortcut.testModal.code:
				// 					that.openTest();
				// 					break;
				// 			}
				// 		}
				// 	}
				// }
				// document.onkeyup = function(e) {
				// 	let event = window.event || e;
					
				// 	let key = event.keyCode;
				// 	if (key == flowConfig.shortcut.dragContainer.code) {
				// 		that.$refs.flowArea.container.dragFlag = false;
				// 	} else if (key == flowConfig.shortcut.scaleContainer.code) {
				// 		event.preventDefault();
				// 		that.$refs.flowArea.container.scaleFlag = false;
				// 	} else if (key == flowConfig.shortcut.multiple.code) {
				// 		that.$refs.flowArea.rectangleMultiple.flag = false;
				// 	}
				// }
				
				// ZFSN.consoleLog(['初始化快捷键成功...']);
			},
			listenPage () {
				window.onbeforeunload = function (e) {
					e = e || window.event;
					if (e) {
						e.returnValue = '关闭提示';
					}
					return '关闭提示';
				};
			},
			initFlow () {
				const that = this;
				
				if (that.flowData.status == flowConfig.flowStatus.CREATE) {
					that.flowData.attr.id = 'flow-' + ZFSN.getId();
				} else {
					that.loadFlow();
				}
				ZFSN.consoleLog(['初始化网络图成功...']);
			},
			loadFlow (json) {
				const that = this;
				
				that.clear();
				let loadData = JSON.parse(json);
				that.flowData.attr = loadData.attr;
				that.flowData.config = loadData.config;
				that.flowData.status = flowConfig.flowStatus.LOADING;
				that.plumb.batch(function() {
					let nodeList = loadData.nodeList;
					nodeList.forEach(function(node, index) {
						that.flowData.nodeList.push(node);
					});
					let linkList = loadData.linkList;
					that.$nextTick(() => {
						linkList.forEach(function(link, index) {
							that.flowData.linkList.push(link);
							let conn = that.plumb.connect({
								source: link.sourceId,
								target: link.targetId,
								anchor: flowConfig.jsPlumbConfig.anchor.default,
								connector: [
									link.cls.linkType,
									{
										gap: 5,
										cornerRadius: 8,
										alwaysRespectStubs: true
									}
								],
								paintStyle: {
									stroke: link.cls.linkColor,
									strokeWidth: link.cls.linkThickness
								}
							});
							if (link.label != '') {
								conn.setLabel({
									label: link.label,
									cssClass: 'linkLabel'
								});
							}
						});
						that.currentSelect = {};
						that.currentSelectGroup = [];
						that.flowData.status = flowConfig.flowStatus.MODIFY;
					});
				}, true);
				let canvasSize = that.computeCanvasSize();
				that.$refs.flowArea.container.pos = {
					top: -canvasSize.minY + 100,
					left: -canvasSize.minX + 100
				}
			},
			findNodeConfig (belongto, type, callback) {
				let node = null;
				switch (belongto) {
					case 'commonNodes':
						node = commonNodes.filter(n => n.type == type);
						break;
					case 'highNodes':
						node = highNodes.filter(n => n.type == type);
						break;
					case 'laneNodes':
						node = laneNodes.filter(n => n.type == type);
						break;
				}
				if (node && node.length >= 0) node = node[0];
				callback(node);
			},
			selectTool (type) {
				let tool = tools.filter(t => t.type == type);
				if (tool && tool.length >= 0) this.currentTool = tool[0];
				
				switch (type) {
					case 'drag':
						this.changeToDrag();
						break;
					case 'connection':
						this.changeToConnection();
						break;
					case 'zoom-in':
						this.changeToZoomIn();
						break;
					case 'zoom-out':
						this.changeToZoomOut();
						break;
				}
			},
			changeToDrag () {
				const that = this;
				
				that.flowData.nodeList.forEach(function(node, index) {
					let f = that.plumb.toggleDraggable(node.id);
					if (!f) {
						that.plumb.toggleDraggable(node.id);
					}
					if (node.type != 'x-lane' && node.type != 'y-lane') {
						that.plumb.unmakeSource(node.id);
						that.plumb.unmakeTarget(node.id);
					}
				});
			},
			changeToConnection() {
				const that = this;
				
				that.flowData.nodeList.forEach(function(node, index) {
					let f = that.plumb.toggleDraggable(node.id);
					if (f) {
						that.plumb.toggleDraggable(node.id);
					}
					if (node.type != 'x-lane' && node.type != 'y-lane') {
						that.plumb.makeSource(node.id, flowConfig.jsPlumbConfig.makeSourceConfig);
						that.plumb.makeTarget(node.id, flowConfig.jsPlumbConfig.makeTargetConfig);
					}
				});
				
				that.currentSelect = {};
				that.currentSelectGroup = [];
			},
			changeToZoomIn () {
				console.log('切换到放大工具');
			},
			changeToZoomOut () {
				console.log('切换到缩小工具');
			},
			checkFlow () {
				const that = this;
				let nodeList = that.flowData.nodeList;
				
				if (nodeList.length <= 0) {
					this.$message.error('图中无任何节点！');
					return false;
				}
				return true;
			},
			saveFlow () {
				const that = this;
				let flowObj = Object.assign({}, that.flowData);
				
				if (!that.checkFlow()) return;
				flowObj.status = flowConfig.flowStatus.SAVE;
				let d = JSON.stringify(flowObj);
				console.log(d);
				this.$message.success('保存成功！');
				return d;
			},
			exportFlowPicture () {
				const that = this;
				
				if (!that.checkFlow()) return;
				
				let $Container = that.$refs.flowArea.$el.children[0],
					svgElems = $($Container).find('svg[id^="link-"]'),
					removeArr = [];
				
				svgElems.each(function(index, svgElem) {
			    	let linkCanvas = document.createElement('canvas');
			    	let canvasId = 'linkCanvas-' + ZFSN.getId();
			    	linkCanvas.id = canvasId;
			    	removeArr.push(canvasId);
			    	
			    	let svgContent = svgElem.outerHTML.trim();
			    	canvg(linkCanvas, svgContent);
			    	if (svgElem.style.position) {
			            linkCanvas.style.position += svgElem.style.position;
			            linkCanvas.style.left += svgElem.style.left;
			            linkCanvas.style.top += svgElem.style.top;
			        }
			    	$($Container).append(linkCanvas);
				});
				
				let canvasSize = that.computeCanvasSize();
				
				let pbd = flowConfig.defaultStyle.photoBlankDistance;
				let offsetPbd = ZFSN.div(pbd, 2);
				
				html2canvas($Container, {
					width: canvasSize.width + pbd,
					height: canvasSize.height + pbd,
					scrollX: -canvasSize.minX + offsetPbd,
					scrollY: -canvasSize.minY + offsetPbd,
					logging: false,
					onclone: function(args) {
						removeArr.forEach(function(id, index) {
							$('#' + id).remove();
						});
					}
				}).then(canvas => {
					let dataURL = canvas.toDataURL('image/png');
					that.flowPicture.url = dataURL;
					that.flowPicture.modalVisible = true;
				});
			},
			downLoadFlowPicture () {
				const that = this;
				let alink = document.createElement('a');
				let alinkId = 'alink-' + ZFSN.getId();
				alink.id = alinkId;
				alink.href = that.flowPicture.url;
				alink.download = '网络图_' + that.flowData.attr.id + '.png';
				alink.click();
			},
			cancelDownLoadFlowPicture () {
				this.flowPicture.url = '';
				this.flowPicture.modalVisible = false;
			},
			computeCanvasSize () {
				const that = this;
				let nodeList = Object.assign([], that.flowData.nodeList), 
					minX = nodeList[0].x, 
					minY = nodeList[0].y, 
					maxX = nodeList[0].x + nodeList[0].width, 
					maxY = nodeList[0].y + nodeList[0].height;
				nodeList.forEach(function(node, index) {
					minX = Math.min(minX, node.x);
					minY = Math.min(minY, node.y);
					maxX = Math.max(maxX, node.x + node.width);
					maxY = Math.max(maxY, node.y + node.height);
				});
				let canvasWidth = maxX - minX;
				let canvasHeight = maxY - minY;
				return {
					width: canvasWidth,
					height: canvasHeight,
					minX: minX,
					minY: minY,
					maxX: maxX,
					maxY: maxY
				};
			},
			clear () {
				const that = this;
				that.flowData.nodeList.forEach(function(node, index) {
					that.plumb.remove(node.id);
				});
				that.currentSelect = {};
				that.currentSelectGroup = [];
				that.flowData.nodeList = [];
				that.flowData.linkList = [];
				that.flowData.remarks = [];
			},
			toggleShowGrid () {
				let flag = this.flowData.config.showGrid;
				if (flag) {
					this.flowData.config.showGrid = false;
					this.flowData.config.showGridText = '显示网格';
					this.flowData.config.showGridIcon = 'eye-invisible';
				} else {
					this.flowData.config.showGrid = true;
					this.flowData.config.showGridText = '隐藏网格';
					this.flowData.config.showGridIcon = 'eye';
				}
			},
			setting () {
				// this.$nextTick(() =>{
				// 	this.$refs.shortcutModal.open();
				// })
				this.$refs.shortcutModal.open();
			},
			shortcutHelper () {
				this.$refs.shortcutModal.open();
			},
			exit () {
				alert('退出网络图绘制...');
			},
			showLinkContextMenu (e) {
				let event = window.event || e;
				
				event.preventDefault();
				event.stopPropagation();
				$('.vue-contextmenuName-flow-menu').css('display', 'none');
				$('.vue-contextmenuName-node-menu').css('display', 'none');
				let x = event.clientX;
				let y = event.clientY;
				this.linkContextMenuData.axis = { x, y };
			},
			deleteLink () {
				const that = this;
				let sourceId = that.currentSelect.sourceId;
				let targetId = that.currentSelect.targetId;
				that.plumb.deleteConnection(that.plumb.getConnections({
					source: sourceId,
					target: targetId
				})[0]);
				let linkList = that.flowData.linkList;
				linkList.splice(linkList.findIndex(link => (link.sourceId == sourceId || link.targetId == targetId)), 1);
				that.currentSelect = {};
			},
			loseShortcut () {
				this.activeShortcut = false;
			},
			getShortcut () {
				this.activeShortcut = true;
			},
			openTest () {
				const that = this;
				
				let flowObj = Object.assign({}, that.flowData);
				that.$refs.testModal.flowData = flowObj;
				that.$refs.testModal.testVisible = true;
			},
			moveNode (type) {
				const that = this;
				
				let m = flowConfig.defaultStyle.movePx, isX = true;
				switch (type) {
					case 'left':
						m = -m;
						break;
					case 'up':
						m = -m;
						isX = false;
						break;
					case 'right':
						break;
					case 'down':
						isX = false;
				}
				
				if (that.currentSelectGroup.length > 0) {
					that.currentSelectGroup.forEach(function(node, index) {
						if (isX) {
							node.x += m;
						} else {
							node.y += m;
						}
					});
					that.plumb.repaintEverything();
				} else if (that.currentSelect.id) {
					if (isX) {
						that.currentSelect.x += m;
					} else {
						that.currentSelect.y += m;
					}
					that.plumb.repaintEverything();
				}
			}
		}
	}
</script>

<style lang="scss">
	@import './style/flow-designer.scss'
</style>
