
<template>
	<div id="wrap">
		<div id="chart-wrap">
			<div id="chart-palette"></div>
			<div id="chart-diagram"></div>
		</div>
		<button @click="onSubmit"></button>
	</div>
</template>
<script>
	import go from 'gojs'
	const MAKE = go.GraphObject.make;
	export default {
		data() {
			return {}
		},
		mounted() {
			var mySelf = this;
			mySelf.myDiagram = MAKE(go.Diagram, "chart-diagram", {
				initialContentAlignment: go.Spot.Center, // 居中显示
				"undoManager.isEnabled": true, // 支持 Ctrl-Z 和 Ctrl-Y 操作
				"toolManager.hoverDelay": 100, //tooltip提示显示延时
				"toolManager.toolTipDuration": 10000, //tooltip持续显示时间
				//isReadOnly:true,//只读
				"grid.visible": true, //显示网格
				allowMove: true, //允许拖动
				// allowDragOut:true,
				allowDelete: true,
				allowCopy: true,
				allowClipboard: true,
				"toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom, //有鼠标滚轮事件放大和缩小，而不是向上和向下滚动
				layout: MAKE(go.TreeLayout, {
					angle: 0,
					layerSpacing: 35
				})
			}); //构建gojs对象
			console.log(mySelf.myDiagram);
			mySelf.myDiagram.addDiagramListener("ObjectSingleClicked", function(e) {
				debugger
				console.log(e.subject.part);
 
			});
 
			mySelf.myDiagram.addDiagramListener("BackgroundSingleClicked", function(e) {
				debugger
				console.log("Double-clicked at" + e.diagram.lastInput.documentPoint);
			});
 
			mySelf.myDiagram.addDiagramListener("ClipboardPasted", function(e) {
				debugger
				console.log("Pasted" + e.diagram.selection.count + "parts");
			});
 
			mySelf.myDiagram.linkTemplate = MAKE(go.Link, {
					curve: go.Link.Bezier
				}, // 贝塞尔曲线
				{
					routing: go.Link.Orthogonal,
					corner: 15
				},
				MAKE(go.Shape, {
					strokeWidth: 2,
					stroke: "#F60"
				}),
				MAKE(go.Shape, {
					toArrow: "Standard",
					fill: "red",
					stroke: "blue"
				}), //箭头
				MAKE(go.TextBlock, {
						margin: 20,
						stroke: "blue",
						font: "14px sans-serif",
						width: 50,
						wrap: go.TextBlock.WrapDesiredSize
					},
					new go.Binding("text", "linktext")), {
					toolTip: MAKE(go.Adornment, "Auto",
						MAKE(go.Shape, {
							fill: "#FFFFCC"
						}),
						MAKE(go.TextBlock, {
							margin: 4
						}, new go.Binding("text", "name1"))
					) // end of Adornment
				}
			);
			let myModel = MAKE(go.GraphLinksModel); //也可以创建link model;需要配置myModel.linkDataArray 如下
			myModel.nodeDataArray = [];
			myModel.linkDataArray = [];
 
			var lightText = "whitesmoke";
			
 
			//Node连接线
		
			mySelf.myDiagram.model = myModel;
		},
		methods: {
			onSubmit() {
 
			},
			init() {
			},
		}
	}
</script>
<style scoped>
	#form-wrap {
		padding: 20px 40px;
		 background-color: white;
		border: solid 1px rgb(244, 244, 244);
	}
 
	#submit {
		width: 102px;
		height: 40px;
		float: right;
		margin: 20px 5px 16px 0;
	}
 
	#chart-wrap {
		width: 100%;
		display: flex;
		justify-content: space-between;
		margin-bottom: 22px;
 
		
	}
 #chart-wrap #chart-palette {
			width: 180px;
			margin-right: 30px;
			background-color: white;
			border: solid 1px rgb(244, 244, 244);
		}
 
		#chart-wrap #chart-diagram {
			flex-grow: 1;
			height: 720px;
			background-color: white;
			border: solid 1px rgb(244, 244, 244);
		}
	#lateEntry {
		clear: both;
		background-color: rgb(255, 255, 255);
		border: solid 1px rgb(244, 244, 244);
 
		
    }
    #lateEntry >span {
			display: inline-block;
			height: 50px;
			font-size: 16px;
			line-height: 50px;
			text-indent: 30px;
			letter-spacing: 0.8px;
			text-align: left;
			color: rgb(35, 35, 35);
			 border-bottom: 1px solid rgb(234, 234, 234);
		}
</style>