package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.jobcommands.AddJobCommand;
import seedu.address.logic.jobcommands.Command;
import seedu.address.logic.jobcommands.DeleteJobCommand;
import seedu.address.logic.jobcommands.ExitCommand;
import seedu.address.logic.jobcommands.FilterCommand;
import seedu.address.logic.jobcommands.SortCommand;
import seedu.address.logic.jobcommands.TagJobCommand;
import seedu.address.logic.jobcommands.UntagJobCommand;
import seedu.address.logic.jobcommands.UpdateJobCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class JobBookCommandParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    private static final Logger logger = LogsCenter.getLogger(JobBookCommandParser.class);

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            //throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        // Note to developers: Change the log level in config.json to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        switch (commandWord) {

        case AddJobCommand.COMMAND_WORD:
            return new AddCommandParser().parse(arguments);

        case DeleteJobCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case TagJobCommand.COMMAND_WORD:
            return new TagCommandParser().parse(arguments);

        case UntagJobCommand.COMMAND_WORD:
            return new UntagCommandParser().parse(arguments);

        case FilterCommand.COMMAND_WORD:
            return new FilterCommandParser().parse(arguments);

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case SortCommand.COMMAND_WORD:
            return new SortCommandParser().parse(arguments);

        case UpdateJobCommand.COMMAND_WORD:
            return new UpdateCommandParser().parse(arguments);

        default:
            logger.finer("This user input caused a ParseException: " + userInput);
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

}
