package com.fm.factorytest.comm.base;

import com.fm.factorytest.comm.bean.Command;
import com.fm.factorytest.comm.vo.CommandVO;

import java.util.LinkedList;

/**
 * Command Wrapper
 *
 * @author lijie
 * @create 2019-02-21 17:02
 **/
public abstract class CommandWrapper {
    protected String cmdID = "";
    protected LinkedList<Command> cmdList;
    protected CommandVO cmdVO;
    protected byte cmd_left;
    protected byte cmd_right;
}
