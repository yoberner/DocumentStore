package edu.yu.cs.com1320.project;

import java.util.*;

public class CommandSet<Target> extends AbstractSet<GenericCommand<Target>> implements Undoable {

    private HashSet<GenericCommand<Target>> genericCommands;

    public CommandSet() {
        this.genericCommands = new HashSet<GenericCommand<Target>>();
    }

    /**
     * does this CommandSet include a command whose target is c?
     *
     * @param c
     * @return
     */
    public boolean containsTarget(Target c) {
        return this.genericCommands.contains(new GenericCommand<>(c,null));
    }

    /**
     * Add a command to this command set.
     * A single Target can only have ONE command in the set.
     *
     * @param genericCommand
     * @throws IllegalArgumentException if this set already contains a command for this Target
     */
    public void addCommand(GenericCommand<Target> genericCommand) {
        if (containsTarget(genericCommand.getTarget())) {
            throw new IllegalArgumentException("this CommandSet already has a command for " + genericCommand.getTarget().toString());
        }
        this.genericCommands.add(genericCommand);
    }

    /**
     * @param c the target to undo
     */
    public boolean undo(Target c) {
        if (containsTarget(c)) {
            Object[] commands = this.genericCommands.toArray();
            for (int i = 0; i < commands.length; i++) {
                GenericCommand<Target> cmd = (GenericCommand<Target>) commands[i];
                if (cmd.getTarget().equals(c)) {
                    this.genericCommands.remove(cmd);
                    return cmd.undo();
                }
            }
        }
        return false;
    }

    /**
     * @return
     */
    @Override
    public boolean undo() {
        int size = this.genericCommands.size();
        return this.undoAll().size() == size;
    }

    /**
     * undo all the commands in this command set.
     * all undone commands are removed from the command set.
     *
     * @return a set of the undone commands
     */
    public Set<GenericCommand<Target>> undoAll() {
        HashSet<GenericCommand<Target>> undone = new HashSet<>(this.genericCommands.size());
        Object[] commands = this.genericCommands.toArray();
        for (int i = 0; i < commands.length; i++) {
            GenericCommand<Target> cmd = (GenericCommand<Target>) commands[i];
            if (cmd.undo()) {
                this.genericCommands.remove(cmd);
                undone.add(cmd);
            }
        }
        return undone;
    }

    @Override
    public Iterator<GenericCommand<Target>> iterator() {
        return this.genericCommands.iterator();
    }

    @Override
    public int size() {
        return this.genericCommands.size();
    }
}