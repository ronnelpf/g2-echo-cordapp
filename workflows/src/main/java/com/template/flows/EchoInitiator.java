package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.node.ServiceHub;
import net.corda.core.node.services.IdentityService;
import java.util.Set;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class EchoInitiator extends FlowLogic<String> {
    private final ProgressTracker progressTracker = new ProgressTracker();
    private final String message;
    private String counterParty;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_CYAN = "\u001B[36m";

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    // Constructor
    public EchoInitiator(String message, String counterParty) {
        this.message = message;
        this.counterParty = counterParty;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {
        // Initiator flow logic goes here.

        // FlowLogic.getServiceHub();
        /* ServiceHub: Get hold of the services that the Corda container provides */
        ServiceHub serviceHub = getServiceHub();

        // ServiceHub.getIdentityService()
        /* IdentityService: Get the IdentityService object that has information about parties & their identities */
        IdentityService identityService = serviceHub.getIdentityService();

        //IdentityService.partiesFromName
        /*  Identity service returns the Party set using the method partiesFromName. Using the boolean flag gives an exact match */
        Set<Party> partySet = identityService.partiesFromName(this.counterParty, true);

        // Retrieve Party
        /* We've made an exact fetch/retrieve using the boolean flag, so we take the only Party available in the Set */
        Party receiver = partySet.iterator().next();

        // initiateFlow(Party/receiver)
        /* FlowLogic abstract class provides initiateFlow
        to send a transaction to a Party [in our case the receiver received as part of the constructor argument]  */
        FlowSession session = initiateFlow(receiver);

        // FlowSession.send(message)
        /*  Using the received session, send the message to the receiver */
        session.send(this.message);
        System.out.println("Message sent to the counter party: " + ANSI_GREEN + receiver.getName() + ANSI_RESET);

        /* If the message is received back, get hold of it using the session's receive method and unwrap the object */
        String echoMsg = session.receive(String.class).unwrap(s -> s);
        System.out.println("Echo Message: " + ANSI_RED + echoMsg + ANSI_RESET);

        // Prints the message in reverse to console
        StringBuilder reversedMsg = new StringBuilder();
        reversedMsg.append(echoMsg);
        reversedMsg = reversedMsg.reverse();
        System.out.println("Reversed Message: " + ANSI_YELLOW +  reversedMsg + ANSI_RESET);

        String messageStatus = ANSI_CYAN + "Message Sent!" + ANSI_RESET;

        return messageStatus;
    }

}
