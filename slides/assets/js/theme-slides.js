ace.define("ace/theme/slides",["require","exports","module","ace/lib/dom"], function(require, exports, module) {

exports.isDark = false;
exports.cssClass = "ace-slides";
exports.cssText = ".ace-slides .ace_gutter {\
background: #ebebeb;\
color: #333;\
overflow : hidden;\
}\
.ace-slides {\
background-color: #f6f7f9;\
color: black;\
}\
.ace-slides .ace_cursor {\
color: transparent;\
}\
.ace-slides .ace_storage,\
.ace-slides .ace_keyword,\
.ace-slides .ace_meta.ace_tag {\
color: rgb(142, 68, 173);\
}\
.ace-slides .ace_lparen,\
.ace-slides .ace_rparen {\
color: rgb(149, 165, 166);\
}\
.ace-slides .ace_keyword.ace_operator {\
color: rgb(0, 0, 0);\
}\
.ace-slides .ace_comment {\
color: rgb(22, 160, 133);\
}\
.ace-slides .ace_symbol {\
color: rgb(41, 128, 185);\
}\
.ace-slides .ace_string {\
color: rgb(230, 126, 34);\
}\
.ace-slides .ace_constant.ace_numeric {\
color: rgb(52, 152, 219);\
}\
";
var dom = require("../lib/dom");
dom.importCssString(exports.cssText, exports.cssClass);
});
