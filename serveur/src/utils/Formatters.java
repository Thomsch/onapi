/* ============================================================================
 * Nom du fichier   : Formatters.java
 * ============================================================================
 * Date de création : 16 mai 2013
 * ============================================================================
 * Auteurs          : Crescenzio Fabio
 *                    Decorvet Grégoire
 *                    Jaquier Kevin
 *                    Schweizer Thomas
 * ============================================================================
 */
package utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Permet de formatter des dates et / ou des heures en chaînes de caractères.
 * 
 * @author Crescenzio Fabio
 * @author Decorvet Grégoire
 * @author Jaquier Kevin
 * @author Schweizer Thomas
 * 
 */
public class Formatters {

   public enum DateFormat {
      DD_MM_YYYY, MM_DD_YYYY, YYYY_MM_DD
   };

   public static String date(int year, int month, int day, DateFormat format) {

      String separator = "-";
      String yyyy = String.format("%04d", year);
      String mm = String.format("%02d", month);
      String dd = String.format("%02d", day);

      switch (format) {
         case MM_DD_YYYY:
            return mm + separator + dd + separator + yyyy;

         case YYYY_MM_DD:
            return yyyy + separator + mm + separator + dd;

         case DD_MM_YYYY:
         default:
            return dd + separator + mm + separator + yyyy;

      }

   }

   public static String date(long time, DateFormat format) {
      Calendar date = Calendar.getInstance();
      date.setTime(new Date(time));

      return date(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH), Formatters.DateFormat.YYYY_MM_DD);

   }

   public static String time(int hours, int minutes, int secondes) {

      String separator = ":";
      String hh = String.format("%02d", hours);
      String mm = String.format("%02d", minutes);
      String ss = String.format("%02d", secondes);

      return hh + separator + mm + separator + ss;

   }

   public static String time(long time) {
      Calendar date = Calendar.getInstance();
      date.setTime(new Date(time));

      return time(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE),
            date.get(Calendar.SECOND));

   }

}