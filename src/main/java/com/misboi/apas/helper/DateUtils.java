package com.misboi.apas.helper;

import java.sql.Timestamp;
import java.time.Instant;

public class DateUtils {

  public static Timestamp getCurrentTimestamp()
  {
	  Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	  
	  return timestamp;
  }
  
  public static Instant getCurrentInstant()
  {
	  Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	  Instant instant = timestamp.toInstant();
	  
	  return instant;
  }
  
  public static void main(String[] args) {

      
      System.out.println(getCurrentTimestamp());
      System.out.println(getCurrentTimestamp().getTime());

      // Convert Timestamp to Instant
      System.out.println(getCurrentInstant());
      System.out.println(getCurrentInstant().toEpochMilli());

      // Convert Instant to Timestamp
      Timestamp tsFromInstant = Timestamp.from(getCurrentInstant());
      System.out.println(tsFromInstant.getTime());
  }
}

