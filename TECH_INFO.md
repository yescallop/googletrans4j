Index - Description Mapping
---
Index | Description
--- | ---
0 | translation
1 | all translations
2 | source language
5 | alternate translations
6 | confidence
7 | spelling correction
8 | language detection
11 | synonyms
12 | definitions
13 | examples
14 | see also

# Query Params

Query Param Keys
---
Key | Description | Values
--- | --- | ---
client | client type | webapp, t, etc.
sl | source language | auto, en, zh-CN, etc.
tl | target language | en, zh-CN, etc.
hl | interface language | en, zh-CN, etc.
dt | required data | listed below
tk | token | [calculated][1] based on tkk and text
q | text (also POST) | text to be translated

Available Values for Query Param "dt"
---
Defined in enum [TransParameter][2].

Value | Index | Description
--- | --- | ---
at | 5 | alternate translations
bd | 1 | all translations
ex | 13 | examples
ld | | unknown
md | 12 | definitions
qca | 7 | spelling correction
rw | 14 | see also
rm | 0 | transliteration
ss | 11 | synonyms
t | 0 | translation
gt | 18 | unknown

[1]: core/src/main/java/cn/yescallop/googletrans4j/TokenTicketUtil.java
[2]: core/src/main/java/cn/yescallop/googletrans4j/TransParameter.java
