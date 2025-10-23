package seedu.job.logic.jobcommands;

import seedu.job.logic.jobcommands.exceptions.JobCommandException;
import seedu.job.model.jobapplication.Model;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws JobCommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model) throws JobCommandException;

}
