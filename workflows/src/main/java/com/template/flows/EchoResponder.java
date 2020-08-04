package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;

// ******************
// * Responder flow *
// ******************
@InitiatedBy(EchoInitiator.class)
public class EchoResponder extends FlowLogic<String> {
    private FlowSession counterpartySession;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RED = "\u001B[31m";

    public EchoResponder(FlowSession counterpartySession) {

        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {
        // Responder flow logic goes here.
        System.out.println("Message received from counter party: " + ANSI_GREEN + counterpartySession.getCounterparty().getName() + ANSI_RESET);
        String receivedMessage = counterpartySession.receive(String.class).unwrap( s -> s);
        System.out.println("Message: " + ANSI_RED + receivedMessage + ANSI_RESET);
        counterpartySession.send(receivedMessage);

        // Prints the message in reverse to console
        StringBuilder reversedMsg = new StringBuilder();
        reversedMsg.append(receivedMessage);
        reversedMsg = reversedMsg.reverse();
        System.out.println("Reversed Message: " + ANSI_YELLOW + reversedMsg +  ANSI_RESET);


        String messageStatus = ANSI_CYAN + "Message Sent!" + ANSI_RESET;

        return messageStatus;

    }
}
