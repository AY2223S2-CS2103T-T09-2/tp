package seedu.address.logic.commands.group;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.Model;
import seedu.address.model.group.GroupNameContainsKeywordsPredicate;

/**
 * Finds and lists all persons in address book who belongs to groups with name containing any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class GroupFindCommand extends GroupCommand {
    public static final String SUB_COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUB_COMMAND_WORD
            + ": Finds group with names that contain any of "
            + "the specified keywords (case-insensitive) "
            + "and displays the people in the group as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " " + SUB_COMMAND_WORD + " CS2101";

    private final GroupNameContainsKeywordsPredicate predicate;

    public GroupFindCommand(GroupNameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredGroupList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_GROUPS_LISTED_OVERVIEW,
                        model.getFilteredGroupList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof GroupFindCommand // instanceof handles nulls
                && predicate.equals(((GroupFindCommand) other).predicate)); // state check
    }
}