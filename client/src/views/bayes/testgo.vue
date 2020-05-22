
<template>
  <div id="chart-diagram" style="width:100%;height:100%;background:#f0f0f0;">
    <div id="myDiagramDiv" style="width:100%; height:100%;"></div>
  </div>
</template>
<script>
import go from "gojs";
const MAKE = go.GraphObject.make;
export default {
  data() {
    return {
      myDiagram: null
    };
  },
  props: {
    model: {
      type: Object,
      default: {
        link: [],
        node: []
      }
    },
    inference: {
      type: Object,
      default() {
        return null
      }
    },
    idMap: {
      type: Object,
      default() {
        return {};
      }
    }
  },
  watch:{
    model(to,from){
      this.setModel()
    },
    inference(to,from){
      this.setModel()
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    findAllPath(linkMap,from,to,result,useless){
      /**
       * 找到from的所有to节点
       * 如果可以到达则将flag设true
       * useless中的包含的表示不可达，直接跳过
       * result中包含表示可达，将flag设true
       * 
       * 返回flag 判断是否可达
       */
      if(to.has(from)){
        return true
      }
      let m = linkMap[from]
      let flag = false
      if(m){
      m.forEach(n => {
        let s = JSON.stringify([from,n])
        
        if(useless.has(s)){
          return
        } 
        if(result.has(s)){
          flag = true
          return
        }
        if(this.findAllPath(linkMap,n,to,result,useless)){
          flag = true
          result.add(s)
        } else {
          useless.add(s)
        }
      })
      }
      return flag
    },
    setModel() {
      let nodeDataArray;
       var linkDataArray = this.model.linkList.map(x => {
        return {
          from: x.sourceId,
          to: x.targetId,
        };
      });
      if (this.inference) {
         let result = new Set()
        let useless = new Set()
        let to = this.inference.evidenceSet
        let linkMap = {}
        this.model.linkList.forEach(x => {
          if(!linkMap[x.targetId]){
            linkMap[x.targetId] = new Set()
          }
          linkMap[x.targetId].add(x.sourceId)
        })
        this.inference.targetSet.forEach(x => {
          this.findAllPath(linkMap,x,to,result,useless)
        })
        let relatedNodes = new Set()
        linkDataArray.forEach((x)=>{
          if(result.has(JSON.stringify([x.to,x.from]))){
            x.color = '#E6A23C'
            if(!this.inference.targetSet.has(x.from)){
              relatedNodes.add(x.from)
            }
          }
        })
        let inferences = this.inference.nodeList.reduce((a,b) => {
          a[b.nodeId] = b
          return a
        },{})
        nodeDataArray = this.model.nodeList.map(x => {
          // let parent = x.sequence;
          // let cpt = x.CPT;
          let data = inferences[x.nodeId]
          let node = this.idMap[x.nodeId]
           let properties = [];
           console.log(node)
          for(let i =0;i < data.probability.length;i++){
            let name = node.value[i].name
            let p = data.probability[i];
            let res ={
              
              width1: p * 100,
              color1: `rgb(${230 * (1 - p) +
                data.borderColor[0] * p},${230 * (1 - p) + data.borderColor[1] * p},
                ${230 * (1 - p) + data.borderColor[2] * p})`,
              tooltip1: "值：\t" + name + "\n概率：\t" + p,
              height1: 10
            }
            properties.push(res)
          }
          // let nodes = parent.map(x => this.idMap[x]);
          // let node = this.idMap[x.nodeId];
          // console.log(node, "node");

          return {
            key: x.nodeId,
            name: x.nodeName,
             background: `rgb(${data.background.join()})`,
              borderColor: relatedNodes.has(x.nodeId)?'#E6A23C':`rgb(${data.borderColor.join()})`,
            properties
          };
        });
      } else {
         nodeDataArray = this.model.nodeList.map(x => {
          let parent = x.sequence;
          let cpt = x.CPT;
          let nodes = parent.map(x => this.idMap[x]);
          let node = this.idMap[x.nodeId];
          console.log(node, "node");
          let infos = [""];
          if (nodes !== null) {
            for (let j of nodes) {
              let newInfos = [];
              for (let k of infos) {
                for (let z of j.value) {
                  newInfos.push(k + "\n" + j.nodeName + "\t：" + z.name);
                }
              }
              infos = newInfos;
            }
          }
          let properties = [];
          console.log(x)
          for (let i = 0; i < cpt.length; i++) {
            let res = {};

            let y = cpt[i];
            for (let j = 0; j < y.length; j++) {
              res["tooltip" + (j + 1)] =
                "概率：" +
                y[j] +
                "\n" +
                x.nodeName +
                "\t：" +
                node.value[j].name +
                infos[i];
              res["width" + (j + 1)] = 20;
              res["height" + (j+1)] = 20;
              res["color" + (j + 1)] = `rgb(${248 * (1 - y[j]) +
                64 * y[j]},${248 * (1 - y[j]) + 159 * y[j]},${248 * (1 - y[j]) + 255 * y[j]})`;
            }
            properties.push(res);
          }
          return {
            key: x.nodeId,
            name: x.nodeName,
           
            properties
          };
        });
      }
      
      if(this.inference){
        
       
      }
     


      this.myDiagram.model = new go.GraphLinksModel(
        nodeDataArray,
        linkDataArray
      );
      console.log(nodeDataArray);
    },
    onSubmit() {},
    init() {
      //   if (window.goSamples) goSamples();  // init for these samples -- you don't need to call this
      var MAKE = go.GraphObject.make; // for conciseness in defining templates
      this.myDiagram = MAKE(
        go.Diagram,
        "myDiagramDiv", // create a Diagram for the DIV HTML element
        {
          // allow double-click in background to create a new node
          //   "clickCreatingTool.archetypeNodeData": { text: "Node", color: "white" },
          // allow Ctrl-G to call groupSelection()
          "commandHandler.archetypeGroupData": {
            text: "Group",
            isGroup: true,
            color: "blue"
          },
          allowDelete:false,
          //  "grid.visible":true,
          // enable undo & redo
          "undoManager.isEnabled": true,
          "toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom
        }
      );

      var nodeSelectionAdornmentTemplate = MAKE(
        go.Adornment,
        "Auto",
        MAKE(go.Shape, {
          fill: null,
          stroke: "deepskyblue",
          strokeWidth: 1.5,
          strokeDashArray: [4, 2]
        }),
        MAKE(go.Placeholder)
      );
      var nodeResizeAdornmentTemplate = MAKE(
        go.Adornment,
        "Spot",
        { locationSpot: go.Spot.Right },
        MAKE(go.Placeholder),
        MAKE(go.Shape, {
          alignment: go.Spot.TopLeft,
          cursor: "nw-resize",
          desiredSize: new go.Size(6, 6),
          fill: "lightblue",
          stroke: "deepskyblue"
        }),
        MAKE(go.Shape, {
          alignment: go.Spot.Top,
          cursor: "n-resize",
          desiredSize: new go.Size(6, 6),
          fill: "lightblue",
          stroke: "deepskyblue"
        }),
        MAKE(go.Shape, {
          alignment: go.Spot.TopRight,
          cursor: "ne-resize",
          desiredSize: new go.Size(6, 6),
          fill: "lightblue",
          stroke: "deepskyblue"
        }),

        MAKE(go.Shape, {
          alignment: go.Spot.Left,
          cursor: "w-resize",
          desiredSize: new go.Size(6, 6),
          fill: "lightblue",
          stroke: "deepskyblue"
        }),
        MAKE(go.Shape, {
          alignment: go.Spot.Right,
          cursor: "e-resize",
          desiredSize: new go.Size(6, 6),
          fill: "lightblue",
          stroke: "deepskyblue"
        }),

        MAKE(go.Shape, {
          alignment: go.Spot.BottomLeft,
          cursor: "se-resize",
          desiredSize: new go.Size(6, 6),
          fill: "lightblue",
          stroke: "deepskyblue"
        }),
        MAKE(go.Shape, {
          alignment: go.Spot.Bottom,
          cursor: "s-resize",
          desiredSize: new go.Size(6, 6),
          fill: "lightblue",
          stroke: "deepskyblue"
        }),
        MAKE(go.Shape, {
          alignment: go.Spot.BottomRight,
          cursor: "sw-resize",
          desiredSize: new go.Size(6, 6),
          fill: "lightblue",
          stroke: "deepskyblue"
        })
      );

      var nodeRotateAdornmentTemplate = MAKE(
        go.Adornment,
        { locationSpot: go.Spot.Center, locationObjectName: "CIRCLE" },
        MAKE(go.Shape, "Circle", {
          name: "CIRCLE",
          cursor: "pointer",
          desiredSize: new go.Size(7, 7),
          fill: "lightblue",
          stroke: "deepskyblue"
        }),
        MAKE(go.Shape, {
          geometryString: "M3.5 7 L3.5 30",
          isGeometryPositioned: true,
          stroke: "deepskyblue",
          strokeWidth: 1.5,
          strokeDashArray: [4, 2]
        })
      );

      // Define the appearance and behavior for Nodes:
      // First, define the shared context menu for all Nodes, Links, and Groups.
      // To simplify this code we define a function for creating a context menu button:
      function makeButton(text, action, visiblePredicate) {
        return MAKE(
          "ContextMenuButton",
          MAKE(go.TextBlock, text),
          { click: action },
          // don't bother with binding GraphObject.visible if there's no predicate
          visiblePredicate
            ? new go.Binding("visible", "", function(o, e) {
                return o.diagram ? visiblePredicate(o, e) : false;
              }).ofObject()
            : {}
        );
      }
      // a context menu is an Adornment with a bunch of buttons in them
      var partContextMenu = MAKE(
        "ContextMenu",
        makeButton("Properties", function(e, obj) {
          // OBJ is this Button
          var contextmenu = obj.part; // the Button is in the context menu Adornment
          var part = contextmenu.adornedPart; // the adornedPart is the Part that the context menu adorns
          // now can do something with PART, or with its data, or with the Adornment (the context menu)
          if (part instanceof go.Link) alert(linkInfo(part.data));
          else if (part instanceof go.Group) alert(groupInfo(contextmenu));
          else alert(nodeInfo(part.data));
        }),
        makeButton(
          "Cut",
          function(e, obj) {
            e.diagram.commandHandler.cutSelection();
          },
          function(o) {
            return o.diagram.commandHandler.canCutSelection();
          }
        ),
        makeButton(
          "Copy",
          function(e, obj) {
            e.diagram.commandHandler.copySelection();
          },
          function(o) {
            return o.diagram.commandHandler.canCopySelection();
          }
        ),
        makeButton(
          "Paste",
          function(e, obj) {
            e.diagram.commandHandler.pasteSelection(
              e.diagram.lastInput.documentPoint
            );
          },
          function(o) {
            return o.diagram.commandHandler.canPasteSelection();
          }
        ),
        makeButton(
          "Delete",
          function(e, obj) {
            e.diagram.commandHandler.deleteSelection();
          },
          function(o) {
            return o.diagram.commandHandler.canDeleteSelection();
          }
        ),
        makeButton(
          "Undo",
          function(e, obj) {
            e.diagram.commandHandler.undo();
          },
          function(o) {
            return o.diagram.commandHandler.canUndo();
          }
        ),
        makeButton(
          "Redo",
          function(e, obj) {
            e.diagram.commandHandler.redo();
          },
          function(o) {
            return o.diagram.commandHandler.canRedo();
          }
        ),
        makeButton(
          "Group",
          function(e, obj) {
            e.diagram.commandHandler.groupSelection();
          },
          function(o) {
            return o.diagram.commandHandler.canGroupSelection();
          }
        ),
        makeButton(
          "Ungroup",
          function(e, obj) {
            e.diagram.commandHandler.ungroupSelection();
          },
          function(o) {
            return o.diagram.commandHandler.canUngroupSelection();
          }
        )
      );

      // These nodes have text surrounded by a rounded rectangle
      // whose fill color is bound to the node data.
      // The user can drag a node by dragging its TextBlock label.
      // Dragging from the Shape will start drawing a new link.

      var propertyTemplate = MAKE(
        go.Panel,
        "TableRow",
        MAKE(
          go.Panel,
          "Auto",
          {
            row: 1,
            column: 1
          },
          MAKE(
            go.Shape,
            "Rectangle",
            {
              fill: "white", // the default fill, if there is no data bound value
              stroke: null,
              width: 0,
              height: 0

              // allow all kinds of links from and to this port
            },
            new go.Binding("width", "width1"),
            new go.Binding("height", "height1"),
            new go.Binding("fill", "color1")
          ),
          {
            // this tooltip Adornment is shared by all nodes
            toolTip: MAKE(
              "ToolTip",
              MAKE(
                go.TextBlock,
                { margin: 4 }, // the tooltip shows the result of calling nodeInfo(data)
                new go.Binding("text", "tooltip1")
              )
            ),
            // this context menu Adornment is shared by all nodes
            contextMenu: partContextMenu
          }
        ),
        MAKE(
          go.Panel,
          "Auto",
          {
            row: 1,
            column: 2
          },
          MAKE(
            go.Shape,
            "Rectangle",
            {
              fill: "white", // the default fill, if there is no data bound value
              stroke: null,
              width: 0,
              height: 0

              // allow all kinds of links from and to this port
            },
            new go.Binding("width", "width2"),
            new go.Binding("fill", "color2"),
            new go.Binding("height", "height2")
          ),
          {
            // this tooltip Adornment is shared by all nodes
            toolTip: MAKE(
              "ToolTip",
              MAKE(
                go.TextBlock,
                { margin: 4 }, // the tooltip shows the result of calling nodeInfo(data)
                new go.Binding("text", "tooltip2")
              )
            ),
            // this context menu Adornment is shared by all nodes
            contextMenu: partContextMenu
          }
        ),
        MAKE(
          go.Panel,
          "Auto",
          {
            row: 1,
            column: 3
          },
          MAKE(
            go.Shape,
            "Rectangle",
            {
              fill: "white", // the default fill, if there is no data bound value
              stroke: null,
              width: 0,
              height: 0

              // allow all kinds of links from and to this port
            },
            new go.Binding("width", "width3"),new go.Binding("height", "height3"),
            new go.Binding("fill", "color3")
          ),
          {
            // this tooltip Adornment is shared by all nodes
            toolTip: MAKE(
              "ToolTip",
              MAKE(
                go.TextBlock,
                { margin: 4 }, // the tooltip shows the result of calling nodeInfo(data)
                new go.Binding("text", "tooltip3")
              )
            ),
            // this context menu Adornment is shared by all nodes
            contextMenu: partContextMenu
          }
        ),
        MAKE(
          go.Panel,
          "Auto",
          {
            row: 1,
            column: 4
          },
          MAKE(
            go.Shape,
            "Rectangle",
            {
              fill: "white", // the default fill, if there is no data bound value
              stroke: null,
              width: 0,
              height: 0

              // allow all kinds of links from and to this port
            },
            new go.Binding("width", "width4"),
            new go.Binding("height", "height4"),
            new go.Binding("fill", "color4")
          ),
          {
            // this tooltip Adornment is shared by all nodes
            toolTip: MAKE(
              "ToolTip",
              MAKE(
                go.TextBlock,
                { margin: 4 }, // the tooltip shows the result of calling nodeInfo(data)
                new go.Binding("text", "tooltip4")
              )
            ),
            // this context menu Adornment is shared by all nodes
            contextMenu: partContextMenu
          }
        ),
        MAKE(
          go.Panel,
          "Auto",
          {
            row: 1,
            column: 5
          },
          MAKE(
            go.Shape,
            "Rectangle",
            {
              fill: "white", // the default fill, if there is no data bound value
              stroke: null,
              width: 0,
              height: 0

              // allow all kinds of links from and to this port
            },
            new go.Binding("width", "width5"),
            new go.Binding("height", "height5"),
            new go.Binding("fill", "color5")
          ),
          {
            // this tooltip Adornment is shared by all nodes
            toolTip: MAKE(
              "ToolTip",
              MAKE(
                go.TextBlock,
                { margin: 4 }, // the tooltip shows the result of calling nodeInfo(data)
                new go.Binding("text", "tooltip5")
              )
            ),
            // this context menu Adornment is shared by all nodes
            contextMenu: partContextMenu
          }
        ),
        MAKE(
          go.Panel,
          "Auto",
          {
            row: 1,
            column: 6
          },
          MAKE(
            go.Shape,
            "Rectangle",
            {
              fill: "white", // the default fill, if there is no data bound value
              stroke: null,
              width: 0,
              height: 0

              // allow all kinds of links from and to this port
            },
            new go.Binding("width", "width6"),
            new go.Binding("height", "height6"),
            new go.Binding("fill", "color6")
          ),
          {
            // this tooltip Adornment is shared by all nodes
            toolTip: MAKE(
              "ToolTip",
              MAKE(
                go.TextBlock,
                { margin: 4 }, // the tooltip shows the result of calling nodeInfo(data)
                new go.Binding("text", "tooltip6")
              )
            ),
            // this context menu Adornment is shared by all nodes
            contextMenu: partContextMenu
          }
        ),
        MAKE(
          go.Panel,
          "Auto",
          {
            row: 1,
            column: 7
          },
          MAKE(
            go.Shape,
            "Rectangle",
            {
              fill: "white", // the default fill, if there is no data bound value
              stroke: null,
              width: 0,
              height: 0

              // allow all kinds of links from and to this port
            },
            new go.Binding("width", "width7"),
            new go.Binding("height", "height7"),
            new go.Binding("fill", "color7")
          ),
          {
            // this tooltip Adornment is shared by all nodes
            toolTip: MAKE(
              "ToolTip",
              MAKE(
                go.TextBlock,
                { margin: 4 }, // the tooltip shows the result of calling nodeInfo(data)
                new go.Binding("text", "tooltip7")
              )
            ),
            // this context menu Adornment is shared by all nodes
            contextMenu: partContextMenu
          }
        ),
        MAKE(
          go.Panel,
          "Auto",
          {
            row: 1,
            column: 8
          },
          MAKE(
            go.Shape,
            "Rectangle",
            {
              fill: "white", // the default fill, if there is no data bound value
              stroke: null,
              width: 0,
              height: 0

              // allow all kinds of links from and to this port
            },
            new go.Binding("width", "width8"),
            new go.Binding("height", "height8"),
            new go.Binding("fill", "color8")
          ),
          {
            // this tooltip Adornment is shared by all nodes
            toolTip: MAKE(
              "ToolTip",
              MAKE(
                go.TextBlock,
                { margin: 4 }, // the tooltip shows the result of calling nodeInfo(data)
                new go.Binding("text", "tooltip8")
              )
            ),
            // this context menu Adornment is shared by all nodes
            contextMenu: partContextMenu
          }
        )
      );

      this.myDiagram.nodeTemplate = MAKE(
        go.Node,
        "Auto",
        {
          locationSpot: go.Spot.Center,
          fromSpot: go.Spot.AllSides,
          toSpot: go.Spot.AllSides
        },
        // 创建一个画板并设置画板的背景色
        MAKE(
          go.Panel,
          "Auto",
          { margin: 0, background: "#fff" },
          // 定义画板的样式
          MAKE(go.Shape, "Rectangle", {
            fill: "#fff",
            stroke: "#33A7EB", // node边框
            strokeWidth: 3,
            fromLinkable: false,
            fromLinkableSelfNode: false,
            fromLinkableDuplicates: false,
            toLinkable: false,
            toLinkableSelfNode: false,
            toLinkableDuplicates: false,
            portId: ""
          },new go.Binding("stroke","borderColor")),

          // 表头样式
          MAKE(
            go.Panel,
            "Table",
            { background: "#ffffff" },
            MAKE(
              go.Panel,
              "TableRow",
              { background: "#E6F7FF" },
              // header
              MAKE(
                go.Picture,
                // Pictures 应该指定宽高.
                // 当没有图片时显示红色的背景
                // 或者当图片为透明的时候也是.
                { margin: 8, width: 20, height: 20, alignment: go.Spot.Left },
                // Picture.source参数值与模型数据中的"source"字段绑定
                new go.Binding("source")
              ),
              MAKE(
                go.TextBlock,
                {
                  alignment: go.Spot.Left,
                  margin: new go.Margin(5, 10, 5, 10),
                  font: "11pt sans-serif", // 表头字体样式调整
                  isMultiline: false,
                  editable: false
                },
                new go.Binding("text", "name").makeTwoWay()
              ),
              new go.Binding("background","background")
            ),
            // properties
            MAKE(
              go.TextBlock,
              "Properties",
              {
                font: "italic 10pt sans-serif"
              },
              new go.Binding("visible", "visible", function(v) {
                return !v;
              }).ofObject("PROPERTIES")
            ),
            MAKE(
              go.Panel,
              "Table",
              { name: "PROPERTIES" },
              new go.Binding("itemArray", "properties"),
              {
                row: 1,
                defaultAlignment: go.Spot.Left,
                padding: new go.Margin(0, 8, 0, 8),
                background: "#ffffff",
                itemTemplate: propertyTemplate
              }
            )
          )
        )
        // MAKE(
        //   go.Panel,
        //   "Auto",
        //   { margin: 0, background: "#fff" },
        //   // 定义画板的样式
        //   MAKE(go.Shape, "Rectangle", {
        //     fill: "#fff",
        //     stroke: "#33A7EB", // node边框
        //     strokeWidth: 3,
        //     fromLinkable: true,
        //     fromLinkableSelfNode: true,
        //     fromLinkableDuplicates: true,
        //     toLinkable: true,
        //     toLinkableSelfNode: true,
        //     toLinkableDuplicates: true,
        //     portId: ""
        //   }),
        //   // 表头样式

        // )
      );
      // this.myDiagram.nodeTemplate = MAKE(
      //   go.Node,
      //   MAKE(
      //     go.Panel,
      //     "Spot",
      //     {
      //       background: "#F5F5F5",
      //       width: 150
      //     },
      //     // MAKE(go.Shape, "RoundedRectangle", {
      //     //   // width: 150,
      //     //   // height: 150,
      //     //   fill: "#aecaea",
      //     //   stroke: null
      //     // }),
      //     // MAKE(go.TextBlock, {
      //     //   //控制文字显示
      //     //   font: "bold 10pt Microsoft YaHei, Arial, sans-serif",
      //     //   stroke: "#5687b3",
      //     //   width: 100,
      //     //   alignment: go.Spot.Center,
      //     //   text: "以蓝色为主块,其他块在蓝色块上的相对位置"
      //     // }),
      //     // MAKE(
      //     //   go.Panel,
      //     //   "Table",
      //     //   { background: "#ffffff",alignment:go.Spot.Center },
      //     //   MAKE(
      //     //     go.Panel,
      //     //     "TableRow",
      //     //     { background: "#E6F7FF" },
      //     //     // header
      //     //     MAKE(
      //     //       go.Picture,
      //     //       // Pictures 应该指定宽高.
      //     //       // 当没有图片时显示红色的背景
      //     //       // 或者当图片为透明的时候也是.
      //     //       { margin: 8, width: 20, height: 20, alignment: go.Spot.Left },
      //     //       // Picture.source参数值与模型数据中的"source"字段绑定
      //     //       new go.Binding("source")
      //     //     ),
      //     //     MAKE(
      //     //       go.TextBlock,
      //     //       {
      //     //         alignment: go.Spot.Left,
      //     //         margin: new go.Margin(5, 0, 5, 10),
      //     //         font: "11pt sans-serif", // 表头字体样式调整
      //     //         isMultiline: false,
      //     //         editable: false
      //     //       },
      //     //       new go.Binding("text", "name").makeTwoWay()
      //     //     )
      //     //   ),
      //     //   // properties
      //     //   MAKE(
      //     //     go.TextBlock,
      //     //     "Properties",
      //     //     {
      //     //       font: "italic 10pt sans-serif"
      //     //     },
      //     //     new go.Binding("visible", "visible", function(v) {
      //     //       return !v;
      //     //     }).ofObject("PROPERTIES")
      //     //   ),
      //     //   MAKE(
      //     //     go.Panel,
      //     //     "Table",
      //     //     { name: "PROPERTIES" },
      //     //     new go.Binding("itemArray", "properties"),
      //     //     {
      //     //       row: 1,
      //     //       defaultAlignment: go.Spot.Left,
      //     //       padding: new go.Margin(0, 8, 0, 8),
      //     //       background: "#ffffff",
      //     //       itemTemplate: propertyTemplate
      //     //     }
      //     //   )
      //     // ),
      //     // MAKE(go.Shape, "RoundedRectangle", {
      //     //   width: 100,
      //     //   height: 100,
      //     //   alignment: go.Spot.TopLeft,
      //     //   fill: "#ffda92",
      //     //   stroke: null
      //     // }),
      //     // MAKE(
      //     //   go.TextBlock,
      //     //   {
      //     //     //控制文字显示
      //     //     font: "bold 16pt Microsoft YaHei, Arial, sans-serif",
      //     //     stroke: "#5687b3",
      //     //     alignment: go.Spot.TopRight,
      //     //     text: "hhhh",
      //     //     textAlign: "center"
      //     //   },
      //     //   new go.Binding("text").makeTwoWay()
      //     // ),
      //      MAKE(
      //       go.Panel,
      //       "Table",
      //       { background: "#ffffff",alignment:go.Spot.Left },
      //       MAKE(
      //         go.Panel,
      //         "TableRow",
      //         { background: "#E6F7FF" },
      //         // header
      //         MAKE(
      //           go.Picture,
      //           // Pictures 应该指定宽高.
      //           // 当没有图片时显示红色的背景
      //           // 或者当图片为透明的时候也是.
      //           { margin: 8, width: 20, height: 20, alignment: go.Spot.Left },
      //           // Picture.source参数值与模型数据中的"source"字段绑定
      //           new go.Binding("source")
      //         ),
      //         MAKE(
      //           go.TextBlock,
      //           {
      //             alignment: go.Spot.Left,
      //             margin: new go.Margin(5, 0, 5, 10),
      //             font: "11pt sans-serif", // 表头字体样式调整
      //             isMultiline: false,
      //             editable: false
      //           },
      //           new go.Binding("text", "name").makeTwoWay()
      //         )
      //       ),
      //       // properties
      //       MAKE(
      //         go.TextBlock,
      //         "Properties",
      //         {
      //           font: "italic 10pt sans-serif"
      //         },
      //         new go.Binding("visible", "visible", function(v) {
      //           return !v;
      //         }).ofObject("PROPERTIES")
      //       ),
      //       MAKE(
      //         go.Panel,
      //         "Table",
      //         { name: "PROPERTIES" },
      //         new go.Binding("itemArray", "properties"),
      //         {
      //           row: 1,
      //           defaultAlignment: go.Spot.Left,
      //           padding: new go.Margin(0, 8, 0, 8),
      //           background: "#ffffff",
      //           itemTemplate: propertyTemplate
      //         }
      //       )
      //     ),
      //   ) //go.Panel的括号
      // ); //go.Node的括号
      function linkInfo(d) {
        // Tooltip info for a link data object
        return "Link:\nfrom " + d.from + " to " + d.to;
      }
      var linkSelectionAdornmentTemplate = MAKE(
        go.Adornment,
        "Link",
        MAKE(
          go.Shape,
          // isPanelMain declares that this Shape shares the Link.geometry
          {
            isPanelMain: true,
            fill: null,
            stroke: "deepskyblue",
            strokeWidth: 0
          }
        ) // use selection object's strokeWidth
      );
      // The link shape and arrowhead have their stroke brush data bound to the "color" property
      this.myDiagram.linkTemplate = MAKE(
        go.Link,
        {
          selectable: true,
          selectionAdornmentTemplate: linkSelectionAdornmentTemplate
        },
        { relinkableFrom: true, relinkableTo: true, reshapable: true },
        {
          toShortLength: 3,
          relinkableFrom: true,
          relinkableTo: true,
          curve: go.Link.Bezier
        }, // allow the user to relink existing links
        MAKE(
          go.Shape,
          {
            strokeWidth: 2,
            stroke: "rgba(0,0,0,0.5)"
          },
          new go.Binding("stroke", "color")
        ),
        MAKE(
          go.Shape,
          { toArrow: "Standard", stroke: null },
          new go.Binding("fill", "color")
        ),
        MAKE(
          go.Panel,
          "Auto",
          MAKE(
            go.TextBlock, // the label text
            {
              textAlign: "center",
              font: "10pt helvetica, arial, sans-serif",
              stroke: "#f00",
              editable: true,
              margin: 0
            },
            new go.Binding("text", "text").makeTwoWay()
          )
        ),
        {
          // this tooltip Adornment is shared by all links
          toolTip: MAKE(
            "ToolTip",
            MAKE(
              go.TextBlock,
              { margin: 4 }, // the tooltip shows the result of calling linkInfo(data)
              new go.Binding("text", "", linkInfo)
            )
          ),
          // the same context menu Adornment is shared by all links
          contextMenu: partContextMenu
        }
      );

      // Define the appearance and behavior for Groups:
      function groupInfo(adornment) {
        // takes the tooltip or context menu, not a group node data object
        var g = adornment.adornedPart; // get the Group that the tooltip adorns
        var mems = g.memberParts.count;
        var links = 0;
        g.memberParts.each(function(part) {
          if (part instanceof go.Link) links++;
        });
        return (
          "Group " +
          g.data.key +
          ": " +
          g.data.text +
          "\n" +
          mems +
          " members including " +
          links +
          " links"
        );
      }
      // Groups consist of a title in the color given by the group node data
      // above a translucent gray rectangle surrounding the member parts
      this.myDiagram.groupTemplate = MAKE(
        go.Group,
        "Vertical",
        {
          selectionObjectName: "PANEL", // selection handle goes around shape, not label
          ungroupable: true // enable Ctrl-Shift-G to ungroup a selected Group
        },
        MAKE(
          go.TextBlock,
          {
            font: "bold 19px sans-serif",
            isMultiline: false, // don't allow newlines in text
            editable: true // allow in-place editing by user
          },
          new go.Binding("text", "text").makeTwoWay(),
          new go.Binding("stroke", "color")
        ),
        MAKE(
          go.Panel,
          "Auto",
          { name: "PANEL" },
          MAKE(
            go.Shape,
            "Rectangle", // the rectangular shape around the members
            {
              fill: "rgba(128,128,128,0.2)",
              stroke: "gray",
              strokeWidth: 3,
              portId: "",
              cursor: "pointer", // the Shape is the port, not the whole Node
              // allow all kinds of links from and to this port
              fromLinkable: true,
              fromLinkableSelfNode: true,
              fromLinkableDuplicates: true,
              toLinkable: true,
              toLinkableSelfNode: true,
              toLinkableDuplicates: true
            }
          ),
          MAKE(go.Placeholder, { margin: 10, background: "transparent" }) // represents where the members are
        ),
        {
          // this tooltip Adornment is shared by all groups
          toolTip: MAKE(
            "ToolTip",
            MAKE(
              go.TextBlock,
              { margin: 4 },
              // bind to tooltip, not to Group.data, to allow access to Group properties
              new go.Binding("text", "", groupInfo).ofObject()
            )
          ),
          // the same context menu Adornment is shared by all groups
          contextMenu: partContextMenu
        }
      );
      // Define the behavior for the Diagram background:
      function diagramInfo(model) {
        // Tooltip info for the diagram's model
        return (
          "Model:\n" +
          model.nodeDataArray.length +
          " nodes, " +
          model.linkDataArray.length +
          " links"
        );
      }
      // provide a tooltip for the background of the Diagram, when not over any Part
      this.myDiagram.toolTip = MAKE(
        "ToolTip",
        MAKE(
          go.TextBlock,
          { margin: 4 },
          new go.Binding("text", "", diagramInfo)
        )
      );
      // provide a context menu for the background of the Diagram, when not over any Part
      this.myDiagram.contextMenu = MAKE(
        "ContextMenu",
        makeButton(
          "Paste",
          function(e, obj) {
            e.diagram.commandHandler.pasteSelection(
              e.diagram.lastInput.documentPoint
            );
          },
          function(o) {
            return o.diagram.commandHandler.canPasteSelection();
          }
        ),
        makeButton(
          "Undo",
          function(e, obj) {
            e.diagram.commandHandler.undo();
          },
          function(o) {
            return o.diagram.commandHandler.canUndo();
          }
        ),
        makeButton(
          "Redo",
          function(e, obj) {
            e.diagram.commandHandler.redo();
          },
          function(o) {
            return o.diagram.commandHandler.canRedo();
          }
        )
      );
      // Create the Diagram's Model:
// if (this.treeLayout) {
        // this.myDiagram.layout = MAKE(go.TreeNetwork);
      // }
      this.setModel();
    }
  }
};
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
#lateEntry > span {
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
