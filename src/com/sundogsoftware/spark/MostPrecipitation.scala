package com.sundogsoftware.spark

import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._
import scala.math.min

/** Find the minimum temperature by weather station */
object MostPrecipitation {
  
  def parseLine(line:String)= {
    val fields = line.split(",")
    val stationID = fields(1)
    val entryType = fields(2)
    val temperature = fields(3).toFloat * 0.1f * (9.0f / 5.0f) + 32.0f
    (stationID, entryType, temperature)
  }
    /** Our main function where the action happens */
  def main(args: Array[String]) {
   
    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    // Create a SparkContext using every core of the local machine
    val sc = new SparkContext("local[*]", "MostPrecipitation")
    
    // Read each line of input data
    val lines = sc.textFile("/Users/ivanmatala/Downloads/SparkScala3/1800.csv")
    
    // Convert to (stationID, entryType, temperature) tuples
    val parsedLines = lines.map(parseLine)
    
    // Filter out all but TMIN entries
    val minTemps = parsedLines.filter(x => x._2 == "PRCP")
    
    // Convert to (stationID, temperature)
    val stationTemps = minTemps.map(x => (x._1, x._3.toFloat))
    // Reduce by stationID retaining the minimum temperature found
    val minTempsByStation = stationTemps.reduce((x,y) => {
      if (x._2 < y._2) y else x
    }) //.reduceByKey( (x,y) => min(x,y))
    
    // Collect, format, and print the results
    //val results = minTempsByStation.collect()
    val results = minTempsByStation
    println(results)
    println(s"$results._1 $results._2")
    /*
    for (result <- results.sorted) {
       val station = result._1
       val temp = result._2
       val formattedTemp = f"$temp%.2f F"
       println(s"$station most precipitation: $formattedTemp") 
    }
    */
      
  }
}