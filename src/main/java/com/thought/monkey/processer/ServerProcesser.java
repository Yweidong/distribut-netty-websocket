package com.thought.monkey.processer;

import com.thought.monkey.session.LocalSession;
import com.thought.monkey.session.SessionManager;
import com.thought.monkey.wsmessage.MessageInfo;

public interface ServerProcesser {

    MessageInfo.CmdType op();
    Boolean action(LocalSession localSession, MessageInfo.Model model);
}
