基本的には `typesafe config` のフォールバック機能を活用して、  
`main/resources/application.conf` や `main/resources/reference.conf` から読み込む。

テスト全般で使用する項目は、`test/resources/application.conf` で上書き/追加する。  
