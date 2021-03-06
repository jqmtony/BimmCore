package me.bimmr.bimmcore.scoreboard;

import com.google.common.base.Splitter;
import me.bimmr.bimmcore.StringUtil;
import me.bimmr.bimmcore.events.timing.TimedEvent;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import java.util.Iterator;

/**
 * A Line on a Board
 */
public class BoardLine {

    private Team   team;
    private String key;
    private int    lineNo;

    private String text;

    private Score score;
    private int   value;

    private Board      board;
    private TimedEvent timedEvent;

    /**
     * Create a BoardLine
     *
     * @param text
     */
    public BoardLine(String text) {
        this(text, -1, null);
    }

    /**
     * Create a BoardLine
     * <p>
     * value = -1
     *
     * @param text
     * @param value
     */
    public BoardLine(String text, int value) {
        this(text, value, null);
    }

    /**
     * Create a BoardLine
     * <p>
     * value = -1
     *
     * @param text
     * @param timedEvent
     */
    public BoardLine(String text, TimedEvent timedEvent) {
        this(text, -1, timedEvent);
    }

    /**
     * Create a BoardLine
     *
     * @param text
     * @param value
     * @param timedEvent
     */
    public BoardLine(String text, int value, TimedEvent timedEvent) {
        this.text = text.substring(0, Math.min(30, text.length()));
        setValue(value);
        setTimedEvent(timedEvent);
    }

    /**
     * Set the board that this BoardLine belongs to
     *
     * @param board
     */
    public void setBoard(Board board) {
        this.board = board;

        this.lineNo = board.getLines().size();
        if (lineNo < 16) {
            if (lineNo > 9) {
                this.key = "" + ChatColor.COLOR_CHAR + (lineNo - 9) + ChatColor.RESET + ChatColor.RESET;
            } else
                this.key = "" + ChatColor.COLOR_CHAR + lineNo + ChatColor.RESET;
            team = board.getScoreboard().registerNewTeam("" + key);

            build();
        }
    }


    /**
     * Get the text
     *
     * @return
     */
    public String getText() {
        return this.text;
    }

    /**
     * Set the text
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text.substring(0, Math.min(30, text.length()));
        build();
    }

    /**
     * Get the value
     *
     * @return
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Set the value
     *
     * @param value
     */
    public void setValue(int value) {
        this.value = value;

        if (score != null)
            this.score.setScore(value);
    }

    /**
     * Get the TimedEvent
     *
     * @return
     */
    public TimedEvent getTimedEvent() {
        return this.timedEvent;
    }

    /**
     * Set the TimedEvent
     *
     * @param timedEvent
     */
    public void setTimedEvent(TimedEvent timedEvent) {
        if (timedEvent != null) {
            this.timedEvent = timedEvent;
            this.timedEvent.setAttachedObject(this);
        }
    }


    /**
     * Build the BoardLine
     */
    public void build() {
        text = StringUtil.addColor(text);

        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
        String prefix = iterator.next();

        if (team == null) {
            if ((team = board.getScoreboard().getTeam("" + key)) == null)
                team = board.getScoreboard().registerNewTeam("" + key);
        }
        team.setPrefix(prefix);

        if (!team.hasEntry(key))
            team.addEntry(key);

        if (score == null)
            score = board.getObjective().getScore(key);

        if (value == -1)
            score.setScore(16 - lineNo);
        else
            score.setScore(value);


        if (text.length() > 16) {
            String prefixColor = ChatColor.getLastColors(prefix);
            String suffix = iterator.next();

            if (prefix.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
                prefix = prefix.substring(0, prefix.length() - 1);
                team.setPrefix(prefix);
                prefixColor = ChatColor.getByChar(suffix.charAt(0)).toString();
                suffix = suffix.substring(1);
            }

            team.setSuffix((prefixColor == null ? ChatColor.RESET : prefixColor) + suffix);

            //System.out.println(team.getPrefix() + "|" + team.getSuffix() + "[" + team.getPrefix().length() + "-" + team.getSuffix().length() + "]");
        }
    }

    /**
     * Get the team
     *
     * @return
     */
    public Team getTeam() {
        return this.team;
    }

    /**
     * Set the Line's team
     *
     * @param team
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Remove a line from a Board
     */
    public void remove() {
        this.board.getScoreboard().resetScores(this.key);
        this.team.unregister();
    }

}
