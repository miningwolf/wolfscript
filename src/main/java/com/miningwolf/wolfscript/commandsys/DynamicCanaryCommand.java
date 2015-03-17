package com.miningwolf.wolfscript.commandsys;
import net.canarymod.commandsys.*;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.Translator;
import java.util.List;
import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;

public class DynamicCanaryCommand extends CanaryCommand {

    private JSFunction executeMethod;
    private JSFunction tabCompleteMethod;
    private ExecutionContext executionContext;
 
    public DynamicCanaryCommand(JSFunction executeMethod, Command meta, CommandOwner owner, JSFunction tabCompleteMethod, ExecutionContext executionContext) {
        super(meta, owner, Translator.getInstance());
        this.executeMethod = executeMethod;
        this.tabCompleteMethod = tabCompleteMethod;
        this.executionContext = executionContext;
    }

    @Override
    protected void execute(MessageReceiver caller, String[] parameters) {
        executionContext.call((JSFunction) executeMethod, owner, caller, parameters);
    }

    @Override
    protected List<String> tabComplete(MessageReceiver caller, String[] parameters) {
         return  (List<String>)executionContext.call((JSFunction) tabCompleteMethod, owner, caller, parameters);
    }
}