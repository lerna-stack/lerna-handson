# Cassandra の基本操作

Cassandra のデータ読み書きは [cqlsh](https://cassandra.apache.org/doc/latest/tools/cqlsh.html) コマンドと [Cassandra Query Language (CQL)](https://cassandra.apache.org/doc/latest/cql/) を使用します。  

## Cassandra に接続する方法

Docker Desktop の Dashboard から Cassandra が動作しているコンテナの CLI を開く。  
CLI の開き方は [Docker Dashboard | Docker Documentation](https://docs.docker.com/desktop/dashboard/#:~:text=Click%20CLI%20to%20open%20a%20terminal%20and%20run%20commands%20on%20the%20container.) から確認できます。  
CLI 内で `cqlsh -u cassandra -p cassandra` と入力します。

## 永続化されているイベントを確認する

キースペース `akka` の テーブル `messages` にイベントが永続化されています。  
永続化されているイベントを確認するためには、cqlsh で次のCQL文を実行します。

```cql
cassandra@cqlsh> select * from akka.messages;

 persistence_id | partition_nr | sequence_nr | timestamp                            | event                                                                                                                                                                                                                                                                                                                      | event_manifest | meta | meta_ser_id | meta_ser_manifest | ser_id    | ser_manifest | tags             | timebucket    | writer_uuid
----------------+--------------+-------------+--------------------------------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+----------------+------+-------------+-------------------+-----------+--------------+------------------+---------------+--------------------------------------
  concerts|RHCP |            0 |           1 | f7c952b2-b2f5-11eb-8352-99a2fb0660f2 |                                                                                                                                                             0x01006578616d706c652e6170706c69636174696f6e2e436f6e636572744576656e7424436f6e63657274437265617465e40101524843d00601e50f050c102d0580c2f29d03474d542b30393a30b0 |                | null |        null |              null | 123454323 |              | {'ConcertEvent'} | 1620802800000 | adc3c202-52f5-478b-bdc0-d7c18cb79cc4
  concerts|RHCP |            0 |           2 | fbbc37c0-b2f5-11eb-8352-99a2fb0660f2 | 0x01006578616d706c652e6170706c69636174696f6e2e436f6e636572744576656e7424436f6e636572745469636b657473426f756768f40101524843d001e50f050c102d0c80e5a3f101474d542b30393a30b001017363616c612e636f6c6c656374696f6e2e696d6d757461626c652e566563746f72b1010101026578616d706c652e616461707465722e436f6e636572745469636b657449e40102 |                | null |        null |              null | 123454323 |              | {'ConcertEvent'} | 1620802800000 | adc3c202-52f5-478b-bdc0-d7c18cb79cc4
  concerts|RHCP |            0 |           3 | fc8b95b2-b2f5-11eb-8352-99a2fb0660f2 | 0x01006578616d706c652e6170706c69636174696f6e2e436f6e636572744576656e7424436f6e636572745469636b657473426f756768f40101524843d001e50f050c102d0dc0bdb59d03474d542b30393a30b001017363616c612e636f6c6c656374696f6e2e696d6d757461626c652e566563746f72b1010101026578616d706c652e616461707465722e436f6e636572745469636b657449e40104 |                | null |        null |              null | 123454323 |              | {'ConcertEvent'} | 1620802800000 | adc3c202-52f5-478b-bdc0-d7c18cb79cc4
  concerts|RHCP |            0 |           4 | 01260560-b2f6-11eb-8352-99a2fb0660f2 |                                                                                                                                                           0x01006578616d706c652e6170706c69636174696f6e2e436f6e636572744576656e7424436f6e6365727443616e63656c6c65e40101524843d001e50f050c102d1580d6b09802474d542b30393a30b0 |                | null |        null |              null | 123454323 |              | {'ConcertEvent'} | 1620802800000 | adc3c202-52f5-478b-bdc0-d7c18cb79cc4

(4 rows)
```

## 永続化されているスナップショットを確認する

キースペース `akka_snapshot` のテーブル `snapshots` にスナップショットが永続化されています。  
永続化されているスナップショットを確認するためには、cqlsh で次のCQL文を実行します。

```cqlsh
cassandra@cqlsh> select * from akka_snapshot.snapshots;

 persistence_id | sequence_nr | meta | meta_ser_id | meta_ser_manifest | ser_id    | ser_manifest | snapshot | snapshot_data                                                                                                                                                                                                                                                                                                                  | timestamp
----------------+-------------+------+-------------+-------------------+-----------+--------------+----------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+---------------
  concerts|RHCP |           4 | null |        null |              null | 123454323 |              |     null | 0x0100cc016578616d706c652e6170706c69636174696f6e2e636f6d6d616e642e6163746f722e44656661756c74436f6e636572744163746f722443616e63656c6c6564436f6e6365727453746174650101524843d001017363616c612e636f6c6c656374696f6e2e696d6d757461626c652e566563746f72b1010101026578616d706c652e616461707465722e436f6e636572745469636b657449e40106 | 1620805521593

(1 rows)
```
