package daylight

import org.shredzone.commons.suncalc.SunTimes
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

object Daylight {
  private const val LAT = 34.8526
  private const val LNG = -82.3940
  private val ZONE: ZoneId = ZoneId.of("America/New_York")
  private val FIXED_ZONE: ZoneId = ZoneId.of("UTC-05:00")
  private val WORK_START: LocalTime = LocalTime.of(9, 0, 0)
  private val WORK_END: LocalTime = LocalTime.of(17, 0, 0)

  @JvmStatic
  fun main(args: Array<String>) {
    val startDate = LocalDate.now()
    val endDate = startDate.plusYears(1)

    var dayCount = 1
    val daysInMonthMap = LinkedHashMap<String, Int>()
    val monthNameFormatter = DateTimeFormatter.ofPattern("MMM yy")

    val csvRows = buildList {
      startDate.datesUntil(endDate).forEach { today ->
        val csvRow = buildList {
          val daysInMonthMapKey = "${monthNameFormatter.format(today)}"
          daysInMonthMap[daysInMonthMapKey] = daysInMonthMap.getOrDefault(daysInMonthMapKey, dayCount - 1) + 1

          add("$today")
          add("$dayCount")
          dayCount++

          val sun = SunTimes.compute()
            .on(today)
            .at(LAT, LNG)
            .timezone(ZONE)
            .execute()

          val civil = SunTimes.compute()
            .on(today)
            .at(LAT, LNG)
            .timezone(ZONE)
            .twilight(SunTimes.Twilight.CIVIL)
            .execute()

          val civilRise = civil.rise!!.withZoneSameInstant(FIXED_ZONE)
          val sunRise = sun.rise!!.withZoneSameInstant(FIXED_ZONE)
          val sunSet = sun.set!!.withZoneSameInstant(FIXED_ZONE)
          val civilSet = civil.set!!.withZoneSameInstant(FIXED_ZONE)
          val workStart = WORK_START.atDate(today).atZone(ZONE).withZoneSameInstant(FIXED_ZONE)
          val workEnd = WORK_END.atDate(today).atZone(ZONE).withZoneSameInstant(FIXED_ZONE)

          add("${civilRise[ChronoField.MINUTE_OF_DAY]}")
          add("${sunRise[ChronoField.MINUTE_OF_DAY]}")
          add("${sunSet[ChronoField.MINUTE_OF_DAY]}")
          add("${civilSet[ChronoField.MINUTE_OF_DAY]}")
          add("${workStart[ChronoField.MINUTE_OF_DAY]}")
          add("${workEnd[ChronoField.MINUTE_OF_DAY]}")

          add("$civilRise")
          add("$sunRise")
          add("$sunSet")
          add("$civilSet")
          add("$workStart")
          add("$workEnd")
        }
        add(csvRow.joinToString(","))
      }
    }


    File("/tmp/input.csv").writeText(csvRows.joinToString("\n"))
    File("/tmp/ytics.csv").writeText("set ytics (\\\n" +
        daysInMonthMap.entries.joinToString("\n") { "\"${it.key}\" ${it.value}, \\" } +
        ")\n")
  }
}
