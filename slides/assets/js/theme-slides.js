ace.define("ace/theme/chrome",["require","exports","module","ace/lib/dom"], function(require, exports, module) {

exports.isDark = false;
exports.cssClass = "ace-chrome";
exports.cssText = ".ace-chrome .ace_gutter {\
background: #ebebeb;\
color: #333;\
overflow : hidden;\
}\
.ace-chrome {\
background-color: #f6f7f9;\
color: black;\
}\
.ace-chrome .ace_cursor {\
color: transparent;\
}\
.ace-chrome .ace_storage,\
.ace-chrome .ace_keyword,\
.ace-chrome .ace_meta.ace_tag {\
color: rgb(142, 68, 173);\
}\
.ace-chrome .ace_lparen,\
.ace-chrome .ace_rparen {\
color: rgb(149, 165, 166);\
}\
.ace-chrome .ace_keyword.ace_operator {\
color: rgb(0, 0, 0);\
}\
.ace-chrome .ace_comment {\
color: rgb(22, 160, 133);\
}\
.ace-chrome .ace_symbol {\
color: rgb(41, 128, 185);\
}\
.ace-chrome .ace_string {\
color: rgb(230, 126, 34);\
}\
.ace-chrome .ace_constant.ace_numeric {\
color: rgb(52, 152, 219);\
}\
";
var dom = require("../lib/dom");
dom.importCssString(exports.cssText, exports.cssClass);
});
