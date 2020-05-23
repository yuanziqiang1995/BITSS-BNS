import Mock from 'mockjs'


export default [
    // {
    //     url: '/dataset/use',
    //     type: 'get',
    //     response: config => {
    //         return {
    //             code: 20000,
    //             data: {
    //                 nodeList: [{
    //                     nodeId: '0',
    //                     nodeName: 'D',
    //                     valueNum: 2,
    //                     value: [{
    //                         id: '0',
    //                         name: '0',
    //                         count: 60,
    //                         probability: 0.6,
    //                     },
    //                     {
    //                         id: '1',
    //                         name: '1',
    //                         count: 40,
    //                         probability: 0.4,
    //                     }]
    //                 },
    //                 {
    //                     nodeId: '1',
    //                     nodeName: 'G',
    //                     valueNum: 3,
    //                     value: [{
    //                         id: '0',
    //                         name: '0',
    //                         count: 30,
    //                         probability: 0.3,
    //                     },
    //                     {
    //                         id: '1',
    //                         name: '1',
    //                         count: 40,
    //                         probability: 0.4,
    //                     },
    //                     {
    //                         id: '2',
    //                         name: '2',
    //                         count: 30,
    //                         probability: 0.3,
    //                     }]
    //                 },
    //                 {
    //                     nodeId: '2',
    //                     nodeName: 'I',
    //                     valueNum: 2,
    //                     value: [{
    //                         id: '0',
    //                         name: '0',
    //                         count: 70,
    //                         probability: 0.7,
    //                     },
    //                     {
    //                         id: '1',
    //                         name: '1',
    //                         count: 30,
    //                         probability: 0.3,
    //                     }]
    //                 },
    //                 {
    //                     nodeId: '3',
    //                     nodeName: 'S',
    //                     valueNum: 2,
    //                     value: [{
    //                         id: '0',
    //                         name: '0',
    //                         count: 20,
    //                         probability: 0.2,
    //                     },
    //                     {
    //                         id: '1',
    //                         name: '1',
    //                         count: 80,
    //                         probability: 0.8,
    //                     }]
    //                 },
    //                 {
    //                     nodeId: '4',
    //                     nodeName: 'L',
    //                     valueNum: 2,
    //                     value: [{
    //                         id: '0',
    //                         name: '0',
    //                         count: 40,
    //                         probability: 0.4,
    //                     },
    //                     {
    //                         id: '1',
    //                         name: '1',
    //                         count: 60,
    //                         probability: 0.6,
    //                     }]
    //                 }]
    //             }
    //         }
    //     }
    // },
    // {
    //     url: '/network/knowledge',
    //     type: 'post',
    //     response: config => {
    //         return {
    //             code: 20000,
    //             data: {
    //                 nodeList: [{
    //                     nodeId: '0',
    //                     nodeName: 'D',
    //                     valueNum: 2,
    //                     CPT: [[0.6, 0.4]],
    //                     sequence: [] //父节点顺序
    //                 },
    //                 {
    //                     nodeId: '1',
    //                     nodeName: 'G',
    //                     valueNum: 3,
    //                     CPT: [[0.3, 0.4, 0.3], [0.05, 0.25, 0.7], [0.9, 0.08, 0.02], [0.5, 0.3, 0.2]],
    //                     sequence: ['2', '0']
    //                 },
    //                 {
    //                     nodeId: '2',
    //                     nodeName: 'I',
    //                     valueNum: 2,
    //                     CPT: [[0.7, 0.3]],
    //                     sequence: []
    //                 },
    //                 {
    //                     nodeId: '3',
    //                     nodeName: 'S',
    //                     valueNum: 2,
    //                     CPT: [[0.95, 0.05], [0.2, 0.8]],
    //                     sequence: ['1']
    //                 },
    //                 {
    //                     nodeId: '4',
    //                     nodeName: 'L',
    //                     valueNum: 2,
    //                     CPT: [[0.1, 0.9], [0.4, 0.6], [0.99, 0.01]],
    //                     sequence: ['2']
    //                 }],
    //                 linkList: [{
    //                     linkId: '0',
    //                     sourceId: '0',
    //                     targetId: '2'
    //                 },
    //                 {
    //                     linkId: '1',
    //                     sourceId: '1',
    //                     targetId: '2'
    //                 },
    //                 {
    //                     linkId: '2',
    //                     sourceId: '1',
    //                     targetId: '3'
    //                 },
    //                 {
    //                     linkId: '3',
    //                     sourceId: '2',
    //                     targetId: '4'
    //                 }]
    //             }
    //         }
    //     }
    // },
    // {
    //     url: '/network/optimize',
    //     type: 'post',
    //     response: config => {
    //         return {
    //             code: 20000,
    //             data: {
    //                 nodeList: [{
    //                     nodeId: '0',
    //                     nodeName: 'D',
    //                     valueNum: 2,
    //                     CPT: [[0.6, 0.4]],
    //                     sequence: []
    //                 },
    //                 {
    //                     nodeId: '1',
    //                     nodeName: 'G',
    //                     valueNum: 3,
    //                     CPT: [[0.3, 0.4, 0.3], [0.05, 0.25, 0.7], [0.9, 0.08, 0.02], [0.5, 0.3, 0.2]],
    //                     sequence: ['2', '0']
    //                 },
    //                 {
    //                     nodeId: '2',
    //                     nodeName: 'I',
    //                     valueNum: 2,
    //                     CPT: [[0.7, 0.3]],
    //                     sequence: []
    //                 },
    //                 {
    //                     nodeId: '3',
    //                     nodeName: 'S',
    //                     valueNum: 2,
    //                     CPT: [[0.9, 0.1], [0.2, 0.8], [0.3, 0.7], [0.5, 0.5], [0.4, 0.6], [0.8, 0.2]],
    //                     sequence: ['1', '2']
    //                 },
    //                 {
    //                     nodeId: '4',
    //                     nodeName: 'L',
    //                     valueNum: 2,
    //                     CPT: [[0.1, 0.9], [0.4, 0.6], [0.99, 0.01]],
    //                     sequence: ['2']
    //                 }],
    //                 Bic: 0.8,
    //                 Bdeu: 0.9,
    //                 K2: 0.95,
    //                 add: [{
    //                     sourceId: '0',
    //                     targetId: '1',
    //                     mutualInf: 0.9
    //                 }],
    //                 delete: [{
    //                     sourceId: '1',
    //                     targetId: '2',
    //                     mutualInf: 0.1
    //                 }]
    //             }
    //         }
    //     }
    // },
    // {
    //     url: '/network/vis',
    //     type: 'post',
    //     response: config => {
    //         return {
    //             code: 20000,
    //             data: {
    //                 CPT: [[[[0.1, 0.9], [0.2, 0.8]]], [[[0.7, 0.3], [0.4, 0.6]]]]
    //             }
    //         }
    //     }
    // },
    // {
    //     url: '/network/inference',
    //     type: 'post',
    //     response: config => {
    //         return {
    //             code: 20000,
    //             data: {
    //                 nodeList: [{
    //                     nodeId: '0',
    //                     nodeName: 'D',
    //                     valueNum: 2,
    //                     probability: [0.15, 0.85]
    //                 },
    //                 {
    //                     nodeId: '1',
    //                     nodeName: 'G',
    //                     valueNum: 3,
    //                     probability: [0.1, 0.8, 0.1]
    //                 },
    //                 {
    //                     nodeId: '2',
    //                     nodeName: 'I',
    //                     valueNum: 2,
    //                     probability: [0.2, 0.8]
    //                 },
    //                 {
    //                     nodeId: '3',
    //                     nodeName: 'S',
    //                     valueNum: 2,
    //                     probability: [0.55, 0.45]
    //                 },
    //                 {
    //                     nodeId: '4',
    //                     nodeName: 'L',
    //                     valueNum: 2,
    //                     probability: [0.75, 0.25]
    //                 }]
    //             }
    //         }
    //     }
    // }
]
