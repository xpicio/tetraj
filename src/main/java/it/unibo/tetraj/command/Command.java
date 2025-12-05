package it.unibo.tetraj.command;

/** Command pattern interface. Encapsulates an action as an object. */
@FunctionalInterface
public interface Command {

  /** Executes the command. */
  void execute();
}
