
<template>
  <div style="width:100%;height:100%;background:#f0f0f0;">
    <div :id="id" style="width:100%; height:100%;"></div>
  </div>
</template>
<script>
export default {
  props: {
    id: {
      type: String,
      required: true
    },
    model: {
      type: Object,
      default() {
        return {};
      }
    },
    readOnly: {
      type: Boolean,
      default: false
    },
    treeLayout: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      myDiagram: null
    };
  },
  mounted() {
    this.init();
  },
  watch: {
    model(to, from) {
      this.modelChanged(to);
    }
  },
  methods: {
    modelChanged(model) {
      let nodeDataArray = model.node.map(x => {
        return {
          key: x.nodeName,
          text: x.nodeName,
          mutual: x.mutual
        };
      });
      let linkDataArray = model.link;
      let maxv = 0.0;
      let minv = 1.0;
      for (let i of linkDataArray) {
        if (i.mutual) {
          if (i.mutual > maxv) {
            maxv = i.mutual;
          }
          if (i.mutual < minv) {
            minv = i.mutual;
          }
        }
      }
      for (let i of linkDataArray) {
        if (i.mutual && !i.color) {
          let mutual = (i.mutual - minv) / (maxv - minv);
          let v = (1 - 1 / ((1 - mutual) * (1 - mutual) + 1)) * 2 * 200;
          // i.width = 10*v + 1
          i.color = `rgb(${v},${v},${v})`;
        }
        // i.color = 'red'
      }
      this.myDiagram.model = new go.GraphLinksModel(
        nodeDataArray,
        linkDataArray
      );
    },
    getModel() {
      return {
        node: this.myDiagram.model.nodeDataArray.map(x => {
          return {
            nodeId: x.key,
            nodeName: x.text,
            mutual: x.mutual
          };
        }),
        link: this.myDiagram.model.linkDataArray
      };
    },
    onSubmit() {},
    init() {
      let go = this.$go;
      //   if (window.goSamples) goSamples();  // init for these samples -- you don't need to call this
      var MAKE = go.GraphObject.make; // for conciseness in defining templates
      this.myDiagram = MAKE(
        go.Diagram,
        this.id, // create a Diagram for the DIV HTML element
        {
          // allow double-click in background to create a new node
          //   "clickCreatingTool.archetypeNodeData": { text: "Node", color: "white" },
          // allow Ctrl-G to call groupSelection()
          "commandHandler.archetypeGroupData": {
            text: "Group",
            isGroup: true,
            color: "blue"
          },
          allowDelete: true,
          // "grid.visible":true,
          // enable undo & redo
          validCycle: go.Diagram.CycleNotDirected,
          "undoManager.isEnabled": !this.readOnly,
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
      function nodeInfo(d) {
        // Tooltip info for a node data object
        var str = "节点名：" + d.text;
        if(d.extra)
        str += "\n互信息：" + d.extra;
        return str;
      }
      // These nodes have text surrounded by a rounded rectangle
      // whose fill color is bound to the node data.
      // The user can drag a node by dragging its TextBlock label.
      // Dragging from the Shape will start drawing a new link.
      this.myDiagram.nodeTemplate = MAKE(
        go.Node,
        "Auto",
        { locationSpot: go.Spot.Center },
        new go.Binding("location", "loc", go.Point.parse).makeTwoWay(
          go.Point.stringify
        ),
        {
          selectable: true,
          selectionAdornmentTemplate: nodeSelectionAdornmentTemplate
        },
        {
          resizable: true,
          resizeObjectName: "PANEL",
          resizeAdornmentTemplate: nodeResizeAdornmentTemplate
        },
        {
          rotatable: true,
          rotateAdornmentTemplate: nodeRotateAdornmentTemplate
        },
        MAKE(
          go.Shape,
          "Rectangle",
          {
            fill: "#E6F7FF", // the default fill, if there is no data bound value
            portId: "",
            cursor: "pointer", // the Shape is the port, not the whole Node
            // allow all kinds of links from and to this port
            fromLinkable: true,
            fromLinkableSelfNode: true,
            fromLinkableDuplicates: true,
            toLinkable: true,
            toLinkableSelfNode: true,
            toLinkableDuplicates: true
          },
          new go.Binding("fill", "color")
        ),
        MAKE(
          go.TextBlock,
          {
            font: "bold 14px sans-serif",
            stroke: "#000",
            margin: 10, // make some extra space for the shape around the text
            isMultiline: false, // don't allow newlines in text
            editable: false // allow in-place editing by user
          },
          new go.Binding("text", "text").makeTwoWay()
        ), // the label shows the node data's text
        {
          // this tooltip Adornment is shared by all nodes
          toolTip: MAKE(
            "ToolTip",
            MAKE(
              go.TextBlock,
              { margin: 4 }, // the tooltip shows the result of calling nodeInfo(data)
              new go.Binding("text", "", nodeInfo)
            )
          ),
          // this context menu Adornment is shared by all nodes
          contextMenu: partContextMenu
        }
      );
      // Define the appearance and behavior for Links:
      function linkInfo(d) {
        // Tooltip info for a link data object
        let str = "连线:\nfrom " + d.from + " to " + d.to;
        
        if(d.color === "#00FF7F"){
          str += '\n建议添加'
        } else if(d.color === 'red'){
          str += '\n互信息：' + d.mutual;
          str += '\n建议删除'
        } else {
          str += '\n互信息：' + d.mutual;
        }
        return str;
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
            strokeDashArray: [0],
            stroke: "rgba(0,0,0,0.5)"
          },
          new go.Binding("stroke", "color"),
          new go.Binding("strokeDashArray", "dash")
        ),
        MAKE(
          go.Shape,
          { toArrow: "Standard", stroke: null },
          new go.Binding("fill", "color")
        ),
        MAKE(
          go.Panel,
          "Auto",
          // MAKE(
          //   go.Shape, // the label background, which becomes transparent around the edges
          //   {
          //     fill: "white",
          //     stroke: null
          //   }
          // ),
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
          "统计信息:\n" +
          model.nodeDataArray.length +
          " 个节点, " +
          model.linkDataArray.length +
          " 条连线"
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
      if (this.treeLayout) {
        // this.myDiagram.layout = MAKE(go.TreeLayout);
      }
      this.myDiagram.commandHandler.canDeleteSelection = e => {
        //用例获取选中的节点或线
        return this.myDiagram.selection.all(function(nodeOrLink) {
          //判断是否存在不允许删除的节点或线
          
          if (!nodeOrLink.data||!nodeOrLink.data.hasOwnProperty("from")) {
            return false;
          } else {
            return true;
          }
        });
      };

      this.myDiagram.addDiagramListener("ChangedSelection", (e, obj) => {
        let data = this.myDiagram.selection.first()
        if(data){
          data = data.data
        }    
        if (!data || data.hasOwnProperty("from")) {
          for (let i of this.model.node) {
            let d = this.myDiagram.model.findNodeDataForKey(i.nodeName);
            this.myDiagram.model.setDataProperty(d, "color", "#E6F7FF");
          }
        } else {
          let mutual = data.mutual;
          let maxv = 0.0;
          let minv = 1.0;
          for (let i in mutual) {
            if (mutual[i] > maxv) {
              maxv = mutual[i];
            }
            if (mutual[i] < minv) {
              minv = mutual[i];
            }
          }
          this.myDiagram.model.setDataProperty(data, "color", "#E6F7FF");
          this.myDiagram.model.setDataProperty(data, "extra", null);
          for (let i in mutual) {
            let d = this.myDiagram.model.findNodeDataForKey(i);
            let v = (mutual[i] - minv) / (maxv - minv);
            let color =
              "rgb(" +
              (255 * (1 - v) + 64 * v) +
              "," +
              (255 * (1 - v) + 159 * v) +
              ",255)";
            this.myDiagram.model.setDataProperty(d, "color", color);
            this.myDiagram.model.setDataProperty(d, "extra",mutual[i]);
          }
          // this.myDiagram.model.setDataProperty(data, 'color', 'purple');
        }
        //  this.$emit('selection-changed',this.myDiagram.selection.first().data)
      });
      // Create the Diagram's Model:
      this.modelChanged(this.model);
    }
  }
};
</script>
<style scoped>
</style>