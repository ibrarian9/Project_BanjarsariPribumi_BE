package com.app.backendhazard.Handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExcelDateConverter {
   public static String formatDate(LocalDateTime dateTime) {
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
       return dateTime.format(formatter);
   }
}
