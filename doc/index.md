# elasticsearch


## 索引
```text
## 查看所有索引
curl -X GET 'hejinyo.cn:9200/_cat/indices?v'

## 创建
curl -X PUT 'hejinyo.cn:9200/skye'

## 删除索引
curl -X DELETE 'hejinyo.cn:9200/skye'

```

## 分词插件

```text
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.5.1.tar.gz

rum -ivh elasticsearch-6.5.1.tar.gz

vim /etc/elasticsearch/elasticsearch.yml
# network.host 0.0.0.0

systemctl start elasticsearch

# 安装ik中文分词插件
https://github.com/medcl/elasticsearch-analysis-ik
./usr/share/elasticsearch/bin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v6.5.1/elasticsearch-analysis-ik-6.5.1.zip

# 安装hanlp中文分词插件
https://github.com/KennFalcon/elasticsearch-analysis-hanlp
./bin/elasticsearch-plugin install https://github.com/KennFalcon/elasticsearch-analysis-hanlp/releases/download/v6.5.1/elasticsearch-analysis-hanlp-6.5.1.zip

# 安装ik拼音分词插件
https://github.com/medcl/elasticsearch-analysis-pinyin
wget https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v6.5.1/elasticsearch-analysis-pinyin-6.5.1.zip


systemctl restart elasticsearch

```

## 创建type 和 字段
```text
curl -X PUT 'hejinyo.cn:9200/skye' -d '
{
	"mappings":{
		"person":{
			"properties":{
				"user":{
					"type":"text",
					"analyzer":"ik_max_word",
					"serach_analyzer":"ik_max_word"
				},
				"title":{
					"type":"text",
					"analyzer":"ik_max_word",
					"serach_analyzer":"ik_max_word"
				},
				"desc":{
					"type":"text",
					"analyzer":"ik_max_word",
					"serach_analyzer":"ik_max_word"
				}
			}
		}
	}
	
}
```

首先新建一个名称为skye的 Index，里面有一个名称为person的 Type。person有三个字段。
user
title
desc
这三个字段都是中文，而且类型都是文本（text），所以需要指定中文分词器，不能使用默认的英文分词器。

Elastic 的分词器称为 analyzer。我们对每个字段指定分词器。
"user": {
  "type": "text",
  "analyzer": "ik_max_word",
  "search_analyzer": "ik_max_word"
}
上面代码中，analyzer是字段文本的分词器，search_analyzer是搜索词的分词器。ik_max_word分词器是插件ik提供的，可以对文本进行最大数量的分词。

## 数据操作

### 新增记录
```text
curl -X PUT 'localhost:9200/skye/person/1' -d '
{
  "user": "张三",
  "title": "工程师",
  "desc": "数据库管理"
}'

```
### 获取记录
```text
curl -X GET 'hejinyo.cn:9200/skye/person/1?pretty=true'

{
  "_index" : "accounts",
  "_type" : "person",
  "_id" : "1",
  "_version" : 1,
  "found" : true,
  "_source" : {
    "user" : "张三",
    "title" : "工程师",
    "desc" : "数据库管理"
  }
}

+ URL 的参数pretty=true表示以易读的格式返回。
+ 返回的数据中，found字段表示查询成功，_source字段返回原始记录

```

## 全文搜索

# 使用 GET 方法，直接请求/Index/Type/_search，就会返回所有记录
```text
hejinyo.cn:9200/skye/person/_search

{
    "took": 5,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": 2, //返回记录数
        "max_score": 1, //最高的匹配程度
        "hits": [ //返回的记录组成的数组
            {
                "_index": "skye",
                "_type": "person",
                "_id": "2",
                "_score": 1, //匹配的程序，默认是按照这个字段降序排列
                "_source": {
                    "desc": "何老师,双双"
                }
            },
            {
                "_index": "skye",
                "_type": "person",
                "_id": "1",
                "_score": 1,
                "_source": {
                    "desc": "架构师,双双"
                }
            }
        ]
    }
}

# 全文搜索
hejinyo.cn:9200/skye/person/_search
{
	"query":{
		"match":{
			"desc":"双"
		}
	},
	"from": 1, //从位置1开始（默认是从位置0开始）
	"size":10 //只返回一条结果
}

# 逻辑运算
{
  "query" : { "match" : { "desc" : "软件 系统" }} //搜索的是软件 or 系统
}'

# 执行多个关键词的and搜索，必须使用布尔查询
{
  "query": {
    "bool": {
      "must": [
        { "match": { "desc": "软件" } },
        { "match": { "desc": "系统" } }
      ]
    }
  }
}

```

