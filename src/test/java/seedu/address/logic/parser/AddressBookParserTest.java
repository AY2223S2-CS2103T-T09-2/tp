package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddIsolatedEventCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.group.GroupCommand;
import seedu.address.logic.commands.group.GroupCreateCommand;
import seedu.address.logic.commands.group.GroupDeleteCommand;
import seedu.address.logic.commands.group.GroupFindCommand;
import seedu.address.logic.commands.group.GroupListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.group.GroupCommandParser;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupNameContainsKeywordsPredicate;
import seedu.address.model.person.MemberOfGroupPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();
    private final GroupCommandParser groupParser = new GroupCommandParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_isolatedEvent() throws Exception {
        AddIsolatedEventCommand command = (AddIsolatedEventCommand) parser.parseCommand(
                AddIsolatedEventCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased() + " "
                        + "ie/biking" + " " + "f/09/03/2023 14:00" + " " + "t/09/03/2023 15:00");
        assertTrue(command.COMMAND_WORD == "event_create");
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_groupCreate() throws Exception {
        GroupCreateCommand command = (GroupCreateCommand) parser.parseCommand(GroupCommand.COMMAND_WORD + " "
                + GroupCreateCommand.SUB_COMMAND_WORD + " g/2103T");
        GroupCreateCommand command2 = (GroupCreateCommand)
                groupParser.parse(GroupCreateCommand.SUB_COMMAND_WORD + " g/2103T");
        assertEquals(new GroupCreateCommand(new Group("2103T")), command);
        assertEquals(command, command2);
    }

    @Test
    public void parseCommand_groupFind() throws Exception {
        List<String> keywords = Arrays.asList("CS2103T", "CS2101");
        GroupFindCommand command = (GroupFindCommand) parser.parseCommand(GroupCommand.COMMAND_WORD + " "
                + GroupFindCommand.SUB_COMMAND_WORD + " "
                + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new GroupFindCommand(new GroupNameContainsKeywordsPredicate(keywords),
                new MemberOfGroupPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_groupDelete() throws Exception {
        GroupDeleteCommand command = (GroupDeleteCommand) parser.parseCommand(GroupCommand.COMMAND_WORD + " "
                + GroupDeleteCommand.SUB_COMMAND_WORD + " 1");
        GroupDeleteCommand command2 = (GroupDeleteCommand)
                groupParser.parse(GroupDeleteCommand.SUB_COMMAND_WORD + " 1");
        assertEquals(new GroupDeleteCommand(Index.fromOneBased(1)), command);
        assertEquals(command, command2);
    }

    @Test
    public void parseCommand_groupList() throws Exception {
        assertTrue(parser.parseCommand(GroupCommand.COMMAND_WORD + " "
                + GroupListCommand.SUB_COMMAND_WORD) instanceof GroupListCommand);
        assertTrue(parser.parseCommand(GroupCommand.COMMAND_WORD + " list") instanceof GroupListCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
