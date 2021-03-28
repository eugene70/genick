package com.github.eugene70.genick

import scala.annotation.tailrec
import scala.collection.mutable
import scala.io.Source
import scala.util.Random

object Dic:
  type WordSeq = Int

  val index = Index
  val dic = mutable.IndexedBuffer[Item]()

  case class Item(term: String, tag: String, category: String)

  object Item:
    def parse(line: String) =
      val wordItem = line.split('\t')
      Item(wordItem(0), wordItem(1), (if (wordItem.size >= 3) wordItem(2) else ""))

  object Index:
    val tagIndex = mutable.HashMap[String, mutable.IndexedBuffer[WordSeq]]()
    val categoryIndex = mutable.HashMap[String, mutable.IndexedBuffer[WordSeq]]()

    def index(wordSeq: Int, item: Item): Unit =
      //tagIndex.get(tag)
      putCategory(item.category, wordSeq)
      putTag(item.tag, wordSeq)

    def random(tag: String) =
      val seqSet = tagIndex.getOrElse(tag, mutable.IndexedBuffer[WordSeq]())
      val randomSeq = new Random().nextInt(seqSet.size)
      seqSet(randomSeq)

    def count(tag: String) =
      tagIndex.getOrElse(tag, Iterable.empty).size

    def putCategory(category: String, wordSeq: WordSeq): Unit =
      val seqSet = categoryIndex.getOrElse(category, mutable.IndexedBuffer[WordSeq]())
      if (!categoryIndex.contains(category))
        categoryIndex.put(category, seqSet)
      seqSet.addOne(wordSeq)

    @tailrec
    def putTag(tag: String, wordSeq: WordSeq): Unit =
      if (tag.isEmpty()) return
      val seqSet = tagIndex.getOrElse(tag, mutable.IndexedBuffer[WordSeq]())
      if (!tagIndex.contains(tag))
        tagIndex.put(tag, seqSet)
      seqSet.addOne(wordSeq)
      // tag를 뒤로부터 한자씩 줄여가며 별도의 인덱스를 만든다.
      // ex) ncn -> nc -> n
      putTag(tag.substring(0, tag.size - 1), wordSeq)

  def load: Unit =
    Source.fromResource("NIADic.tsv").getLines()
      .map(Item.parse)
      .filter(filter)
      .foreach(add)

  def add(wordItem: Item): Unit =
    val wordSeqToAdd = dic.size
    dic.addOne(wordItem)
    index.index(wordSeqToAdd, wordItem)

  def filter(wordItem: Item) = {
    wordItem.category != "category" &&
      wordItem.category != "brand_name" &&
      wordItem.category != "people_names" &&
      wordItem.category != "brand_name" &&
      wordItem.category != "special_characters"
  }

  def randomWord(tag: String): Item =
    dic(index.random(tag))

  @main def testDic = {
    /*
    조합법:
      mm(관형사) + n(체언)
      ma(부사) + 하는 + n(체언)
      p(용언) + n(체언) - 용언의 형태소 분석 필요
     */
    Dic.load
    index.categoryIndex.toVector
      .sortBy(_._1)
      .foreach((k, v) => println(k + " " + v.size))
    index.tagIndex.toVector
      .sortBy(_._1)
      .foreach((k, v) => println(k + " " + v.size))
    Range.Int(0, 10, 1).foreach(n => {
      print(Dic.randomWord("mm").term.toString + " ")
      println(Dic.randomWord("n").term)
      print(Dic.randomWord("ma").toString + "하는 ")
      println(Dic.randomWord("n"))
      print(Dic.randomWord("p").term + "는 ")
      println(Dic.randomWord("n"))
      print(Dic.randomWord("n").toString + " ")
      println(Dic.randomWord("p"))
    })
  }
