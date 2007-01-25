/*
 * This class borrows extensively from the Log4J DailyRollingFileAppender 
 * written by Eirik Lygre and Ceki Gulcu and copyrighted to the Apache Foundation
 * under the Apache 2 License (http://apache.org/licenses/LICENSE-2.0).
 */

package org.opensaml.log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A minor refactoring of Log4J's DailyRollingFileAppender. The log4j appender does not provide an easy mechanism for
 * having a file name of name.date.extension, instead it wants to do name.extension.date which on some platforms can be
 * a pain. This appender is meant to handle this case.
 * 
 * The file appender will create a file called filename.extension, then it will roll over the files to
 * filename.date.extension. The default date pattern is "'.'yyyy-MM-dd" (i.e. daily roll over) and the default file
 * extnsion is '.log'.
 * 
 * @author Chad La Joie
 */
public class RollingFileAppender extends FileAppender {

    // The code assumes that the following constants are in a increasing
    // sequence.
    static final int TOP_OF_TROUBLE = -1;

    static final int TOP_OF_MINUTE = 0;

    static final int TOP_OF_HOUR = 1;

    static final int HALF_DAY = 2;

    static final int TOP_OF_DAY = 3;

    static final int TOP_OF_WEEK = 4;

    static final int TOP_OF_MONTH = 5;

    /**
     * The date pattern. By default, the pattern is set to "'.'yyyy-MM-dd" meaning daily rollover.
     */
    private String datePattern = "'-'yyyy-MM-dd";

    /**
     * The log file will be renamed to the value of the scheduledFilename variable when the next interval is entered.
     * For example, if the rollover period is one hour, the log file will be renamed to the value of "scheduledFilename"
     * at the beginning of the next hour.
     * 
     * The precise time when a rollover occurs depends on logging activity.
     */
    private String scheduledFilename;

    /**
     * The next time we estimate a rollover should occur.
     */
    private long nextCheck = System.currentTimeMillis() - 1;

    /**
     * Current date
     */
    private Date now = new Date();

    /**
     * The date pattern
     */
    private SimpleDateFormat sdf;

    /**
     * Calendar for determing roll over information
     */
    private RollingCalendar rc = new RollingCalendar();

    /**
     * How often to check for roll over
     */
    int checkPeriod = TOP_OF_TROUBLE;

    /**
     * The gmtTimeZone is used only in computeCheckPeriod() method.
     */
    static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");

    /**
     * The file extension
     */
    private String fileExtension = ".log";

    /**
     * The file name without the date or extension components.
     */
    private String simpleFileName;

    /**
     * Default constructor
     */
    public RollingFileAppender() {

    }

    /**
     * Constructor.
     * 
     * @param layout the log entry layout pattern
     * @param filename the file name
     * @param datePattern the date pattern used to determine rolling behavior
     * @param fileExtension the file extension to post-pend to log file name
     * 
     * @throws IOException thrown if the file can not be created
     */
    public RollingFileAppender(Layout layout, String filename, String datePattern, String fileExtension)
            throws IOException {
        super(layout, filename + fileExtension, true);
        simpleFileName = filename;
        setDatePattern(datePattern);
        setFileExtension(fileExtension);
        activateOptions();
    }

    /**
     * Gets the extension post-pended to the file name.
     * 
     * @return the log file's extension
     */
    public String getFileExtension() {
        return fileExtension;
    }

    /**
     * Sets the extension post-pended to the file name.
     * 
     * @param extension the log file's extension
     */
    public void setFileExtension(String extension) {
        fileExtension = extension;
    }

    /**
     * The <b>DatePattern</b> takes a string in the same format as expected by {@link SimpleDateFormat}. This options
     * determines the rollover schedule.
     * 
     * @param pattern the rollover date pattern
     */
    public void setDatePattern(String pattern) {
        datePattern = pattern;
    }

    /**
     * Returns the value of the <b>DatePattern</b> option.
     * 
     * @return the rollover date pattern
     */
    public String getDatePattern() {
        return datePattern;
    }

    public void activateOptions() {
        super.activateOptions();
        if (datePattern != null && fileName != null) {
            now.setTime(System.currentTimeMillis());
            sdf = new SimpleDateFormat(datePattern);
            int type = computeCheckPeriod();
            printPeriodicity(type);
            rc.setType(type);
            File file = new File(fileName);
            scheduledFilename = simpleFileName + sdf.format(new Date(file.lastModified())) + fileExtension;

        } else {
            LogLog.error("Either File or DatePattern options are not set for appender [" + name + "].");
        }
    }

    void printPeriodicity(int type) {
        switch (type) {
            case TOP_OF_MINUTE:
                LogLog.debug("Appender [" + name + "] to be rolled every minute.");
                break;
            case TOP_OF_HOUR:
                LogLog.debug("Appender [" + name + "] to be rolled on top of every hour.");
                break;
            case HALF_DAY:
                LogLog.debug("Appender [" + name + "] to be rolled at midday and midnight.");
                break;
            case TOP_OF_DAY:
                LogLog.debug("Appender [" + name + "] to be rolled at midnight.");
                break;
            case TOP_OF_WEEK:
                LogLog.debug("Appender [" + name + "] to be rolled at start of week.");
                break;
            case TOP_OF_MONTH:
                LogLog.debug("Appender [" + name + "] to be rolled at start of every month.");
                break;
            default:
                LogLog.warn("Unknown periodicity for appender [" + name + "].");
        }
    }

    // This method computes the roll over period by looping over the
    // periods, starting with the shortest, and stopping when the r0 is
    // different from from r1, where r0 is the epoch formatted according
    // the datePattern (supplied by the user) and r1 is the
    // epoch+nextMillis(i) formatted according to datePattern. All date
    // formatting is done in GMT and not local format because the test
    // logic is based on comparisons relative to 1970-01-01 00:00:00
    // GMT (the epoch).

    int computeCheckPeriod() {
        RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.ENGLISH);
        Date epoch = new Date(0);
        if (datePattern != null) {
            for (int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
                simpleDateFormat.setTimeZone(gmtTimeZone); // do all date formatting in GMT
                String r0 = simpleDateFormat.format(epoch);
                rollingCalendar.setType(i);
                Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
                String r1 = simpleDateFormat.format(next);
                if (r0 != null && r1 != null && !r0.equals(r1)) {
                    return i;
                }
            }
        }
        return TOP_OF_TROUBLE; // Deliberately head for trouble...
    }

    /**
     * Rollover the current file to a new file.
     */
    void rollOver() {

        /* Compute filename, but only if datePattern is specified */
        if (datePattern == null) {
            errorHandler.error("Missing DatePattern option in rollOver().");
            return;
        }

        String datedFilename = simpleFileName + sdf.format(now) + fileExtension;
        // It is too early to roll over because we are still within the
        // bounds of the current interval. Rollover will occur once the
        // next interval is reached.
        if (scheduledFilename.equals(datedFilename)) {
            return;
        }

        // close current file, and rename it to datedFilename
        this.closeFile();

        File target = new File(scheduledFilename);
        if (target.exists()) {
            target.delete();
        }

        File file = new File(fileName);
        boolean result = file.renameTo(target);
        if (result) {
            LogLog.debug(fileName + " -> " + scheduledFilename);
        } else {
            LogLog.error("Failed to rename [" + fileName + "] to [" + scheduledFilename + "].");
        }

        try {
            // This will also close the file. This is OK since multiple
            // close operations are safe.
            this.setFile(fileName, false, this.bufferedIO, this.bufferSize);
        } catch (IOException e) {
            errorHandler.error("setFile(" + fileName + ", false) call failed.");
        }
        scheduledFilename = datedFilename;
    }

    /**
     * This method differentiates DailyRollingFileAppender from its super class.
     * 
     * <p>
     * Before actually logging, this method will check whether it is time to do a rollover. If it is, it will schedule
     * the next rollover time and then rollover.
     */
    protected void subAppend(LoggingEvent event) {
        long n = System.currentTimeMillis();
        if (n >= nextCheck) {
            now.setTime(n);
            nextCheck = rc.getNextCheckMillis(now);
            rollOver();
        }
        super.subAppend(event);
    }
}

/**
 * RollingCalendar is a helper class to DailyRollingFileAppender. Given a periodicity type and the current time, it
 * computes the start of the next interval.
 */
class RollingCalendar extends GregorianCalendar {

    /**
     * Serial Number
     */
    private static final long serialVersionUID = -1818276930015758128L;

    int type = RollingFileAppender.TOP_OF_TROUBLE;

    RollingCalendar() {
        super();
    }

    RollingCalendar(TimeZone tz, Locale locale) {
        super(tz, locale);
    }

    void setType(int type) {
        this.type = type;
    }

    public long getNextCheckMillis(Date now) {
        return getNextCheckDate(now).getTime();
    }

    public Date getNextCheckDate(Date now) {
        this.setTime(now);

        switch (type) {
            case RollingFileAppender.TOP_OF_MINUTE:
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.MINUTE, 1);
                break;
            case RollingFileAppender.TOP_OF_HOUR:
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case RollingFileAppender.HALF_DAY:
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                int hour = get(Calendar.HOUR_OF_DAY);
                if (hour < 12) {
                    this.set(Calendar.HOUR_OF_DAY, 12);
                } else {
                    this.set(Calendar.HOUR_OF_DAY, 0);
                    this.add(Calendar.DAY_OF_MONTH, 1);
                }
                break;
            case RollingFileAppender.TOP_OF_DAY:
                this.set(Calendar.HOUR_OF_DAY, 0);
                this.set(Calendar.MINUTE, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.DATE, 1);
                break;
            case RollingFileAppender.TOP_OF_WEEK:
                this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
                this.set(Calendar.HOUR_OF_DAY, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case RollingFileAppender.TOP_OF_MONTH:
                this.set(Calendar.DATE, 1);
                this.set(Calendar.HOUR_OF_DAY, 0);
                this.set(Calendar.SECOND, 0);
                this.set(Calendar.MILLISECOND, 0);
                this.add(Calendar.MONTH, 1);
                break;
            default:
                throw new IllegalStateException("Unknown periodicity type.");
        }
        return getTime();
    }
}