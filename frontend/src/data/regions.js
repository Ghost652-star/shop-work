const provinces = [
  { code: '110000', name: '北京市' },
  { code: '120000', name: '天津市' },
  { code: '130000', name: '河北省' },
  { code: '140000', name: '山西省' },
  { code: '150000', name: '内蒙古自治区' },
  { code: '210000', name: '辽宁省' },
  { code: '220000', name: '吉林省' },
  { code: '230000', name: '黑龙江省' },
  { code: '310000', name: '上海市' },
  { code: '320000', name: '江苏省' },
  { code: '330000', name: '浙江省' },
  { code: '340000', name: '安徽省' },
  { code: '350000', name: '福建省' },
  { code: '360000', name: '江西省' },
  { code: '370000', name: '山东省' },
  { code: '410000', name: '河南省' },
  { code: '420000', name: '湖北省' },
  { code: '430000', name: '湖南省' },
  { code: '440000', name: '广东省' },
  { code: '450000', name: '广西壮族自治区' },
  { code: '460000', name: '海南省' },
  { code: '500000', name: '重庆市' },
  { code: '510000', name: '四川省' },
  { code: '520000', name: '贵州省' },
  { code: '530000', name: '云南省' },
  { code: '540000', name: '西藏自治区' },
  { code: '610000', name: '陕西省' },
  { code: '620000', name: '甘肃省' },
  { code: '630000', name: '青海省' },
  { code: '640000', name: '宁夏回族自治区' },
  { code: '650000', name: '新疆维吾尔自治区' },
  { code: '710000', name: '台湾省' },
  { code: '810000', name: '香港特别行政区' },
  { code: '820000', name: '澳门特别行政区' }
]

const cities = {
  '110000': [{ code: '110100', name: '北京市' }],
  '120000': [{ code: '120100', name: '天津市' }],
  '130000': [
    { code: '130100', name: '石家庄市' }, { code: '130200', name: '唐山市' },
    { code: '130300', name: '秦皇岛市' }, { code: '130400', name: '邯郸市' },
    { code: '130500', name: '邢台市' }, { code: '130600', name: '保定市' },
    { code: '130700', name: '张家口市' }, { code: '130800', name: '承德市' },
    { code: '130900', name: '沧州市' }, { code: '131000', name: '廊坊市' },
    { code: '131100', name: '衡水市' }
  ],
  '140000': [
    { code: '140100', name: '太原市' }, { code: '140200', name: '大同市' },
    { code: '140300', name: '阳泉市' }, { code: '140400', name: '长治市' },
    { code: '140500', name: '晋城市' }, { code: '140600', name: '朔州市' },
    { code: '140700', name: '晋中市' }, { code: '140800', name: '运城市' },
    { code: '140900', name: '忻州市' }, { code: '141000', name: '临汾市' },
    { code: '141100', name: '吕梁市' }
  ],
  '150000': [
    { code: '150100', name: '呼和浩特市' }, { code: '150200', name: '包头市' },
    { code: '150300', name: '乌海市' }, { code: '150400', name: '赤峰市' },
    { code: '150500', name: '通辽市' }, { code: '150600', name: '鄂尔多斯市' },
    { code: '150700', name: '呼伦贝尔市' }, { code: '150800', name: '巴彦淖尔市' },
    { code: '150900', name: '乌兰察布市' }
  ],
  '210000': [
    { code: '210100', name: '沈阳市' }, { code: '210200', name: '大连市' },
    { code: '210300', name: '鞍山市' }, { code: '210400', name: '抚顺市' },
    { code: '210500', name: '本溪市' }, { code: '210600', name: '丹东市' },
    { code: '210700', name: '锦州市' }, { code: '210800', name: '营口市' },
    { code: '210900', name: '阜新市' }, { code: '211000', name: '辽阳市' },
    { code: '211100', name: '盘锦市' }, { code: '211200', name: '铁岭市' },
    { code: '211300', name: '朝阳市' }, { code: '211400', name: '葫芦岛市' }
  ],
  '220000': [
    { code: '220100', name: '长春市' }, { code: '220200', name: '吉林市' },
    { code: '220300', name: '四平市' }, { code: '220400', name: '辽源市' },
    { code: '220500', name: '通化市' }, { code: '220600', name: '白山市' },
    { code: '220700', name: '松原市' }, { code: '220800', name: '白城市' }
  ],
  '230000': [
    { code: '230100', name: '哈尔滨市' }, { code: '230200', name: '齐齐哈尔市' },
    { code: '230300', name: '鸡西市' }, { code: '230400', name: '鹤岗市' },
    { code: '230500', name: '双鸭山市' }, { code: '230600', name: '大庆市' },
    { code: '230700', name: '伊春市' }, { code: '230800', name: '佳木斯市' },
    { code: '230900', name: '七台河市' }, { code: '231000', name: '牡丹江市' },
    { code: '231100', name: '黑河市' }, { code: '231200', name: '绥化市' }
  ],
  '310000': [{ code: '310100', name: '上海市' }],
  '320000': [
    { code: '320100', name: '南京市' }, { code: '320200', name: '无锡市' },
    { code: '320300', name: '徐州市' }, { code: '320400', name: '常州市' },
    { code: '320500', name: '苏州市' }, { code: '320600', name: '南通市' },
    { code: '320700', name: '连云港市' }, { code: '320800', name: '淮安市' },
    { code: '320900', name: '盐城市' }, { code: '321000', name: '扬州市' },
    { code: '321100', name: '镇江市' }, { code: '321200', name: '泰州市' },
    { code: '321300', name: '宿迁市' }
  ],
  '330000': [
    { code: '330100', name: '杭州市' }, { code: '330200', name: '宁波市' },
    { code: '330300', name: '温州市' }, { code: '330400', name: '嘉兴市' },
    { code: '330500', name: '湖州市' }, { code: '330600', name: '绍兴市' },
    { code: '330700', name: '金华市' }, { code: '330800', name: '衢州市' },
    { code: '330900', name: '舟山市' }, { code: '331000', name: '台州市' },
    { code: '331100', name: '丽水市' }
  ],
  '340000': [
    { code: '340100', name: '合肥市' }, { code: '340200', name: '芜湖市' },
    { code: '340300', name: '蚌埠市' }, { code: '340400', name: '淮南市' },
    { code: '340500', name: '马鞍山市' }, { code: '340600', name: '淮北市' },
    { code: '340700', name: '铜陵市' }, { code: '340800', name: '安庆市' },
    { code: '341000', name: '黄山市' }, { code: '341100', name: '滁州市' },
    { code: '341200', name: '阜阳市' }, { code: '341300', name: '宿州市' },
    { code: '341500', name: '六安市' }, { code: '341600', name: '亳州市' },
    { code: '341700', name: '池州市' }, { code: '341800', name: '宣城市' }
  ],
  '350000': [
    { code: '350100', name: '福州市' }, { code: '350200', name: '厦门市' },
    { code: '350300', name: '莆田市' }, { code: '350400', name: '三明市' },
    { code: '350500', name: '泉州市' }, { code: '350600', name: '漳州市' },
    { code: '350700', name: '南平市' }, { code: '350800', name: '龙岩市' },
    { code: '350900', name: '宁德市' }
  ],
  '360000': [
    { code: '360100', name: '南昌市' }, { code: '360200', name: '景德镇市' },
    { code: '360300', name: '萍乡市' }, { code: '360400', name: '九江市' },
    { code: '360500', name: '新余市' }, { code: '360600', name: '鹰潭市' },
    { code: '360700', name: '赣州市' }, { code: '360800', name: '吉安市' },
    { code: '360900', name: '宜春市' }, { code: '361000', name: '抚州市' },
    { code: '361100', name: '上饶市' }
  ],
  '370000': [
    { code: '370100', name: '济南市' }, { code: '370200', name: '青岛市' },
    { code: '370300', name: '淄博市' }, { code: '370400', name: '枣庄市' },
    { code: '370500', name: '东营市' }, { code: '370600', name: '烟台市' },
    { code: '370700', name: '潍坊市' }, { code: '370800', name: '济宁市' },
    { code: '370900', name: '泰安市' }, { code: '371000', name: '威海市' },
    { code: '371100', name: '日照市' }, { code: '371300', name: '临沂市' },
    { code: '371400', name: '德州市' }, { code: '371500', name: '聊城市' },
    { code: '371600', name: '滨州市' }, { code: '371700', name: '菏泽市' }
  ],
  '410000': [
    { code: '410100', name: '郑州市' }, { code: '410200', name: '开封市' },
    { code: '410300', name: '洛阳市' }, { code: '410400', name: '平顶山市' },
    { code: '410500', name: '安阳市' }, { code: '410600', name: '鹤壁市' },
    { code: '410700', name: '新乡市' }, { code: '410800', name: '焦作市' },
    { code: '410900', name: '濮阳市' }, { code: '411000', name: '许昌市' },
    { code: '411100', name: '漯河市' }, { code: '411200', name: '三门峡市' },
    { code: '411300', name: '南阳市' }, { code: '411400', name: '商丘市' },
    { code: '411500', name: '信阳市' }, { code: '411600', name: '周口市' },
    { code: '411700', name: '驻马店市' }
  ],
  '420000': [
    { code: '420100', name: '武汉市' }, { code: '420200', name: '黄石市' },
    { code: '420300', name: '十堰市' }, { code: '420500', name: '宜昌市' },
    { code: '420600', name: '襄阳市' }, { code: '420700', name: '鄂州市' },
    { code: '420800', name: '荆门市' }, { code: '420900', name: '孝感市' },
    { code: '421000', name: '荆州市' }, { code: '421100', name: '黄冈市' },
    { code: '421200', name: '咸宁市' }, { code: '421300', name: '随州市' }
  ],
  '430000': [
    { code: '430100', name: '长沙市' }, { code: '430200', name: '株洲市' },
    { code: '430300', name: '湘潭市' }, { code: '430400', name: '衡阳市' },
    { code: '430500', name: '邵阳市' }, { code: '430600', name: '岳阳市' },
    { code: '430700', name: '常德市' }, { code: '430800', name: '张家界市' },
    { code: '430900', name: '益阳市' }, { code: '431000', name: '郴州市' },
    { code: '431100', name: '永州市' }, { code: '431200', name: '怀化市' },
    { code: '431300', name: '娄底市' }
  ],
  '440000': [
    { code: '440100', name: '广州市' }, { code: '440200', name: '韶关市' },
    { code: '440300', name: '深圳市' }, { code: '440400', name: '珠海市' },
    { code: '440500', name: '汕头市' }, { code: '440600', name: '佛山市' },
    { code: '440700', name: '江门市' }, { code: '440800', name: '湛江市' },
    { code: '440900', name: '茂名市' }, { code: '441200', name: '肇庆市' },
    { code: '441300', name: '惠州市' }, { code: '441400', name: '梅州市' },
    { code: '441500', name: '汕尾市' }, { code: '441600', name: '河源市' },
    { code: '441700', name: '阳江市' }, { code: '441800', name: '清远市' },
    { code: '441900', name: '东莞市' }, { code: '442000', name: '中山市' },
    { code: '445100', name: '潮州市' }, { code: '445200', name: '揭阳市' },
    { code: '445300', name: '云浮市' }
  ],
  '450000': [
    { code: '450100', name: '南宁市' }, { code: '450200', name: '柳州市' },
    { code: '450300', name: '桂林市' }, { code: '450400', name: '梧州市' },
    { code: '450500', name: '北海市' }, { code: '450600', name: '防城港市' },
    { code: '450700', name: '钦州市' }, { code: '450800', name: '贵港市' },
    { code: '450900', name: '玉林市' }, { code: '451000', name: '百色市' },
    { code: '451100', name: '贺州市' }, { code: '451200', name: '河池市' },
    { code: '451300', name: '来宾市' }, { code: '451400', name: '崇左市' }
  ],
  '460000': [
    { code: '460100', name: '海口市' }, { code: '460200', name: '三亚市' },
    { code: '460300', name: '三沙市' }, { code: '460400', name: '儋州市' }
  ],
  '500000': [{ code: '500100', name: '重庆市' }],
  '510000': [
    { code: '510100', name: '成都市' }, { code: '510300', name: '自贡市' },
    { code: '510400', name: '攀枝花市' }, { code: '510500', name: '泸州市' },
    { code: '510600', name: '德阳市' }, { code: '510700', name: '绵阳市' },
    { code: '510800', name: '广元市' }, { code: '510900', name: '遂宁市' },
    { code: '511000', name: '内江市' }, { code: '511100', name: '乐山市' },
    { code: '511300', name: '南充市' }, { code: '511400', name: '眉山市' },
    { code: '511500', name: '宜宾市' }, { code: '511600', name: '广安市' },
    { code: '511700', name: '达州市' }, { code: '511800', name: '雅安市' },
    { code: '511900', name: '巴中市' }, { code: '512000', name: '资阳市' }
  ],
  '520000': [
    { code: '520100', name: '贵阳市' }, { code: '520200', name: '六盘水市' },
    { code: '520300', name: '遵义市' }, { code: '520400', name: '安顺市' },
    { code: '520500', name: '毕节市' }, { code: '520600', name: '铜仁市' }
  ],
  '530000': [
    { code: '530100', name: '昆明市' }, { code: '530200', name: '曲靖市' },
    { code: '530300', name: '玉溪市' }, { code: '530400', name: '保山市' },
    { code: '530500', name: '昭通市' }, { code: '530600', name: '丽江市' },
    { code: '530700', name: '普洱市' }, { code: '530800', name: '临沧市' }
  ],
  '540000': [
    { code: '540100', name: '拉萨市' }, { code: '540200', name: '日喀则市' },
    { code: '540300', name: '昌都市' }, { code: '540400', name: '林芝市' },
    { code: '540500', name: '山南市' }, { code: '540600', name: '那曲市' },
    { code: '542500', name: '阿里地区' }
  ],
  '610000': [
    { code: '610100', name: '西安市' }, { code: '610200', name: '铜川市' },
    { code: '610300', name: '宝鸡市' }, { code: '610400', name: '咸阳市' },
    { code: '610500', name: '渭南市' }, { code: '610600', name: '延安市' },
    { code: '610700', name: '汉中市' }, { code: '610800', name: '榆林市' },
    { code: '610900', name: '安康市' }, { code: '611000', name: '商洛市' }
  ],
  '620000': [
    { code: '620100', name: '兰州市' }, { code: '620200', name: '嘉峪关市' },
    { code: '620300', name: '金昌市' }, { code: '620400', name: '白银市' },
    { code: '620500', name: '天水市' }, { code: '620600', name: '武威市' },
    { code: '620700', name: '张掖市' }, { code: '620800', name: '平凉市' },
    { code: '620900', name: '酒泉市' }, { code: '621000', name: '庆阳市' },
    { code: '621100', name: '定西市' }, { code: '621200', name: '陇南市' }
  ],
  '630000': [
    { code: '630100', name: '西宁市' }, { code: '630200', name: '海东市' }
  ],
  '640000': [
    { code: '640100', name: '银川市' }, { code: '640200', name: '石嘴山市' },
    { code: '640300', name: '吴忠市' }, { code: '640400', name: '固原市' },
    { code: '640500', name: '中卫市' }
  ],
  '650000': [
    { code: '650100', name: '乌鲁木齐市' }, { code: '650200', name: '克拉玛依市' },
    { code: '650400', name: '吐鲁番市' }, { code: '650500', name: '哈密市' },
    { code: '652300', name: '昌吉回族自治州' }, { code: '652700', name: '博尔塔拉蒙古自治州' },
    { code: '652800', name: '巴音郭楞蒙古自治州' }, { code: '652900', name: '阿克苏地区' },
    { code: '653000', name: '克孜勒苏柯尔克孜自治州' }, { code: '653100', name: '喀什地区' },
    { code: '653200', name: '和田地区' }, { code: '654000', name: '伊犁哈萨克自治州' },
    { code: '654200', name: '塔城地区' }, { code: '654300', name: '阿勒泰地区' }
  ]
}

const districts = {
  '110100': [
    { code: '110101', name: '东城区' }, { code: '110102', name: '西城区' },
    { code: '110105', name: '朝阳区' }, { code: '110106', name: '丰台区' },
    { code: '110107', name: '石景山区' }, { code: '110108', name: '海淀区' },
    { code: '110109', name: '门头沟区' }, { code: '110111', name: '房山区' },
    { code: '110112', name: '通州区' }, { code: '110113', name: '顺义区' },
    { code: '110114', name: '昌平区' }, { code: '110115', name: '大兴区' },
    { code: '110116', name: '怀柔区' }, { code: '110117', name: '平谷区' },
    { code: '110118', name: '密云区' }, { code: '110119', name: '延庆区' }
  ],
  '120100': [
    { code: '120101', name: '和平区' }, { code: '120102', name: '河东区' },
    { code: '120103', name: '河西区' }, { code: '120104', name: '南开区' },
    { code: '120105', name: '河北区' }, { code: '120106', name: '红桥区' },
    { code: '120110', name: '东丽区' }, { code: '120111', name: '西青区' },
    { code: '120112', name: '津南区' }, { code: '120113', name: '北辰区' },
    { code: '120114', name: '武清区' }, { code: '120115', name: '宝坻区' },
    { code: '120116', name: '滨海新区' }, { code: '120117', name: '宁河区' },
    { code: '120118', name: '静海区' }, { code: '120119', name: '蓟州区' }
  ],
  '130100': [
    { code: '130102', name: '长安区' }, { code: '130104', name: '桥西区' },
    { code: '130105', name: '新华区' }, { code: '130107', name: '井陉矿区' },
    { code: '130108', name: '裕华区' }, { code: '130109', name: '藁城区' },
    { code: '130110', name: '鹿泉区' }, { code: '130111', name: '栾城区' },
    { code: '130112', name: '井陉县' }, { code: '130121', name: '正定县' },
    { code: '130123', name: '灵寿县' }, { code: '130125', name: '行唐县' },
    { code: '130126', name: '深泽县' }, { code: '130127', name: '无极县' },
    { code: '130128', name: '赵县' }, { code: '130129', name: '元氏县' },
    { code: '130130', name: '赞皇县' }, { code: '130131', name: '平山县' },
    { code: '130132', name: '晋州市' }, { code: '130133', name: '新乐市' }
  ],
  '130200': [
    { code: '130202', name: '路南区' }, { code: '130203', name: '路北区' },
    { code: '130204', name: '古冶区' }, { code: '130205', name: '开平区' },
    { code: '130207', name: '丰南区' }, { code: '130208', name: '丰润区' },
    { code: '130209', name: '曹妃甸区' }, { code: '130224', name: '滦南县' },
    { code: '130225', name: '乐亭县' }, { code: '130227', name: '迁西县' },
    { code: '130229', name: '玉田县' }, { code: '130281', name: '遵化市' },
    { code: '130283', name: '迁安市' }, { code: '130284', name: '滦州市' }
  ],
  '310100': [
    { code: '310101', name: '黄浦区' }, { code: '310104', name: '徐汇区' },
    { code: '310105', name: '长宁区' }, { code: '310106', name: '静安区' },
    { code: '310107', name: '普陀区' }, { code: '310109', name: '虹口区' },
    { code: '310110', name: '杨浦区' }, { code: '310112', name: '闵行区' },
    { code: '310113', name: '宝山区' }, { code: '310114', name: '嘉定区' },
    { code: '310115', name: '浦东新区' }, { code: '310116', name: '金山区' },
    { code: '310117', name: '松江区' }, { code: '310118', name: '青浦区' },
    { code: '310120', name: '奉贤区' }, { code: '310151', name: '崇明区' }
  ],
  '320100': [
    { code: '320102', name: '玄武区' }, { code: '320104', name: '秦淮区' },
    { code: '320105', name: '建邺区' }, { code: '320106', name: '鼓楼区' },
    { code: '320111', name: '浦口区' }, { code: '320113', name: '栖霞区' },
    { code: '320114', name: '雨花台区' }, { code: '320115', name: '江宁区' },
    { code: '320116', name: '六合区' }, { code: '320117', name: '溧水区' },
    { code: '320118', name: '高淳区' }
  ],
  '320500': [
    { code: '320505', name: '虎丘区' }, { code: '320506', name: '吴中区' },
    { code: '320507', name: '相城区' }, { code: '320508', name: '姑苏区' },
    { code: '320509', name: '吴江区' }, { code: '320571', name: '苏州工业园区' },
    { code: '320582', name: '常熟市' }, { code: '320583', name: '昆山市' },
    { code: '320585', name: '太仓市' }, { code: '320581', name: '张家港市' }
  ],
  '330100': [
    { code: '330102', name: '上城区' }, { code: '330105', name: '拱墅区' },
    { code: '330106', name: '西湖区' }, { code: '330108', name: '滨江区' },
    { code: '330109', name: '萧山区' }, { code: '330110', name: '余杭区' },
    { code: '330111', name: '富阳区' }, { code: '330112', name: '临安区' },
    { code: '330113', name: '临平区' }, { code: '330114', name: '钱塘区' },
    { code: '330122', name: '桐庐县' }, { code: '330127', name: '淳安县' },
    { code: '330182', name: '建德市' }
  ],
  '330200': [
    { code: '330203', name: '海曙区' }, { code: '330205', name: '江北区' },
    { code: '330206', name: '北仑区' }, { code: '330211', name: '镇海区' },
    { code: '330212', name: '鄞州区' }, { code: '330213', name: '奉化区' },
    { code: '330225', name: '象山县' }, { code: '330226', name: '宁海县' },
    { code: '330281', name: '余姚市' }, { code: '330282', name: '慈溪市' }
  ],
  '350200': [
    { code: '350203', name: '思明区' }, { code: '350205', name: '海沧区' },
    { code: '350206', name: '湖里区' }, { code: '350211', name: '集美区' },
    { code: '350212', name: '同安区' }, { code: '350213', name: '翔安区' }
  ],
  '350500': [
    { code: '350502', name: '鲤城区' }, { code: '350503', name: '丰泽区' },
    { code: '350504', name: '洛江区' }, { code: '350505', name: '泉港区' },
    { code: '350521', name: '惠安县' }, { code: '350524', name: '安溪县' },
    { code: '350525', name: '永春县' }, { code: '350526', name: '德化县' },
    { code: '350581', name: '石狮市' }, { code: '350582', name: '晋江市' },
    { code: '350583', name: '南安市' }
  ],
  '370100': [
    { code: '370102', name: '历下区' }, { code: '370103', name: '市中区' },
    { code: '370104', name: '槐荫区' }, { code: '370105', name: '天桥区' },
    { code: '370112', name: '历城区' }, { code: '370113', name: '长清区' },
    { code: '370114', name: '章丘区' }, { code: '370115', name: '济阳区' },
    { code: '370116', name: '莱芜区' }, { code: '370117', name: '钢城区' },
    { code: '370124', name: '平阴县' }, { code: '370126', name: '商河县' }
  ],
  '370200': [
    { code: '370202', name: '市南区' }, { code: '370203', name: '市北区' },
    { code: '370211', name: '黄岛区' }, { code: '370212', name: '崂山区' },
    { code: '370213', name: '李沧区' }, { code: '370214', name: '城阳区' },
    { code: '370215', name: '即墨区' }, { code: '370281', name: '胶州市' },
    { code: '370283', name: '平度市' }, { code: '370285', name: '莱西市' }
  ],
  '410100': [
    { code: '410102', name: '中原区' }, { code: '410103', name: '二七区' },
    { code: '410104', name: '管城回族区' }, { code: '410105', name: '金水区' },
    { code: '410106', name: '上街区' }, { code: '410108', name: '惠济区' },
    { code: '410122', name: '中牟县' }, { code: '410181', name: '巩义市' },
    { code: '410182', name: '荥阳市' }, { code: '410183', name: '新密市' },
    { code: '410184', name: '新郑市' }, { code: '410185', name: '登封市' }
  ],
  '420100': [
    { code: '420102', name: '江岸区' }, { code: '420103', name: '江汉区' },
    { code: '420104', name: '硚口区' }, { code: '420105', name: '汉阳区' },
    { code: '420106', name: '武昌区' }, { code: '420107', name: '洪山区' },
    { code: '420111', name: '青山区' }, { code: '420112', name: '东西湖区' },
    { code: '420113', name: '汉南区' }, { code: '420114', name: '蔡甸区' },
    { code: '420115', name: '江夏区' }, { code: '420116', name: '黄陂区' },
    { code: '420117', name: '新洲区' }, { code: '420118', name: '孝南区' }
  ],
  '430100': [
    { code: '430102', name: '芙蓉区' }, { code: '430103', name: '天心区' },
    { code: '430104', name: '岳麓区' }, { code: '430105', name: '开福区' },
    { code: '430111', name: '雨花区' }, { code: '430112', name: '望城区' },
    { code: '430121', name: '长沙县' }, { code: '430181', name: '浏阳市' },
    { code: '430182', name: '宁乡市' }
  ],
  '440100': [
    { code: '440103', name: '荔湾区' }, { code: '440104', name: '越秀区' },
    { code: '440105', name: '海珠区' }, { code: '440106', name: '天河区' },
    { code: '440111', name: '白云区' }, { code: '440112', name: '黄埔区' },
    { code: '440113', name: '番禺区' }, { code: '440114', name: '花都区' },
    { code: '440115', name: '南沙区' }, { code: '440117', name: '从化区' },
    { code: '440118', name: '增城区' }
  ],
  '440300': [
    { code: '440303', name: '罗湖区' }, { code: '440304', name: '福田区' },
    { code: '440305', name: '南山区' }, { code: '440306', name: '宝安区' },
    { code: '440307', name: '龙岗区' }, { code: '440308', name: '盐田区' },
    { code: '440309', name: '龙华区' }, { code: '440310', name: '坪山区' },
    { code: '440311', name: '光明区' }
  ],
  '440600': [
    { code: '440604', name: '禅城区' }, { code: '440605', name: '南海区' },
    { code: '440606', name: '顺德区' }, { code: '440607', name: '三水区' },
    { code: '440608', name: '高明区' }
  ],
  '441900': [
    { code: '441901', name: '东城街道' }, { code: '441902', name: '南城街道' },
    { code: '441903', name: '万江街道' }, { code: '441904', name: '莞城街道' }
  ],
  '500100': [
    { code: '500101', name: '万州区' }, { code: '500102', name: '涪陵区' },
    { code: '500103', name: '渝中区' }, { code: '500104', name: '大渡口区' },
    { code: '500105', name: '江北区' }, { code: '500106', name: '沙坪坝区' },
    { code: '500107', name: '九龙坡区' }, { code: '500108', name: '南岸区' },
    { code: '500109', name: '北碚区' }, { code: '500110', name: '綦江区' },
    { code: '500111', name: '大足区' }, { code: '500112', name: '渝北区' },
    { code: '500113', name: '巴南区' }, { code: '500114', name: '黔江区' },
    { code: '500115', name: '长寿区' }, { code: '500116', name: '江津区' },
    { code: '500117', name: '合川区' }, { code: '500118', name: '永川区' },
    { code: '500119', name: '南川区' }, { code: '500120', name: '璧山区' },
    { code: '500151', name: '铜梁区' }, { code: '500152', name: '潼南区' },
    { code: '500153', name: '荣昌区' }, { code: '500154', name: '开州区' },
    { code: '500155', name: '梁平区' }, { code: '500156', name: '武隆区' }
  ],
  '510100': [
    { code: '510104', name: '锦江区' }, { code: '510105', name: '青羊区' },
    { code: '510106', name: '金牛区' }, { code: '510107', name: '武侯区' },
    { code: '510108', name: '成华区' }, { code: '510112', name: '龙泉驿区' },
    { code: '510113', name: '青白江区' }, { code: '510114', name: '新都区' },
    { code: '510115', name: '温江区' }, { code: '510116', name: '双流区' },
    { code: '510117', name: '郫都区' }, { code: '510121', name: '金堂县' },
    { code: '510129', name: '大邑县' }, { code: '510131', name: '蒲江县' },
    { code: '510132', name: '新津区' }, { code: '510181', name: '都江堰市' },
    { code: '510182', name: '彭州市' }, { code: '510183', name: '邛崃市' },
    { code: '510184', name: '崇州市' }, { code: '510185', name: '简阳市' }
  ],
  '610100': [
    { code: '610102', name: '新城区' }, { code: '610103', name: '碑林区' },
    { code: '610104', name: '莲湖区' }, { code: '610111', name: '灞桥区' },
    { code: '610112', name: '未央区' }, { code: '610113', name: '雁塔区' },
    { code: '610114', name: '阎良区' }, { code: '610115', name: '临潼区' },
    { code: '610116', name: '长安区' }, { code: '610117', name: '高陵区' },
    { code: '610118', name: '鄠邑区' }
  ]
}

export const regionOptions = provinces.map(p => ({
  value: p.code,
  label: p.name,
  children: (cities[p.code] || []).map(c => ({
    value: c.code,
    label: c.name,
    children: (districts[c.code] || []).map(d => ({
      value: d.code,
      label: d.name
    }))
  }))
}))

export const getProvinces = () => provinces
export const getCities = (provinceCode) => cities[provinceCode] || []
export const getDistricts = (cityCode) => districts[cityCode] || []

const codeToNameMap = {}
provinces.forEach(p => { codeToNameMap[p.code] = p.name })
Object.values(cities).flat().forEach(c => { codeToNameMap[c.code] = c.name })
Object.values(districts).flat().forEach(d => { codeToNameMap[d.code] = d.name })

const nameToCodeMap = {}
provinces.forEach(p => { nameToCodeMap[p.name] = p.code })
Object.values(cities).flat().forEach(c => { nameToCodeMap[c.name] = c.code })
Object.values(districts).flat().forEach(d => { nameToCodeMap[d.name] = d.code })

export const getCodeToName = (code) => codeToNameMap[code] || code
export const getNameToCode = (name) => nameToCodeMap[name] || name
