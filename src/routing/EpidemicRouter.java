/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package routing;

import assignment.ActorSystem;
import assignment.Company;
import assignment.CompanyPreference;
import assignment.Student;
import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;

import java.util.Map;
import java.util.Random;

/**
 * Epidemic message router with drop-oldest buffer and only single transferring
 * connections at a time.
 */
public class EpidemicRouter extends ActiveRouter {
	private final static String COMPANY_TO_STUDENT = "companyToStudent";
	private final static String STUDENT_TO_STUDENT = "studentToStudent";
	private final static Random rand = new Random();

	/**
	 * Constructor. Creates a new message router based on the settings in
	 * the given Settings object.
	 * @param s The settings object
	 */
	public EpidemicRouter(Settings s) {
		super(s);
		//TODO: read&use epidemic router specific settings (if any)
	}

	/**
	 * Copy constructor.
	 * @param r The router prototype where setting values are copied from
	 */
	protected EpidemicRouter(EpidemicRouter r) {
		super(r);
		//TODO: copy epidemic settings here (if any)
	}

	@Override
	public void update() {
		super.update();
		if (isTransferring() || !canStartTransfer()) {
			return; // transferring, don't try other connections yet
		}

		// Try first the messages that can be delivered to final recipient
		if (exchangeDeliverableMessages() != null) {
			return; // started a transfer, don't try others (yet)
		}

		// then try any/all message to any/all connection
		this.tryAllMessagesToAllConnections();
	}

	@Override
	public boolean createNewMessage(Message msg) {
		boolean fromIsCompany = ActorSystem.withActor(msg.getFrom().getAddress(), actor -> actor instanceof Company);
		boolean toIsCompany = ActorSystem.withActor(msg.getTo().getAddress(), actor -> actor instanceof Company);

		// Mark the messages appropriately as coming
		// from companies to students (e.g. companies advertising themselves in the booth)
		// or from student to student (e.g. students exchanging their impression on companies)
		if (fromIsCompany && !toIsCompany) {
			msg.addProperty(COMPANY_TO_STUDENT, true);
		} else if (!fromIsCompany && !toIsCompany) {
			msg.addProperty(STUDENT_TO_STUDENT, true);
		}

		return super.createNewMessage(msg);
	}

	@Override
	public int receiveMessage(Message m, DTNHost from) {
		if (m.getProperty(COMPANY_TO_STUDENT) != null) {
			// Get company
			Company company = ActorSystem.withActor(m.getFrom().getAddress(), actor -> (Company) actor);
			// Get student passing it by
			CompanyPreference toPreference = ActorSystem.withActor(m.getTo().getAddress(), actor -> ((Student) actor).getPreference());
			// With a random chance dependent on the company appeal,
			// update the appeal in students preference list
			if (rand.nextDouble() * 15 < company.getAppeal()) {
				toPreference.updateCompanyAppeal(company.getID(), company.getAppeal());
			}
		} else if (m.getProperty(STUDENT_TO_STUDENT) != null) {
			// Get the company preference list of sending student
			CompanyPreference fromPreference = ActorSystem.withActor(m.getFrom().getAddress(), actor -> ((Student) actor).getPreference());
			// Get the company preference list of receiving student
			CompanyPreference toPreference = ActorSystem.withActor(m.getTo().getAddress(), actor -> ((Student) actor).getPreference());
			// For each subjectively most appealing company, pass this preference to
			// the receiving student
			for (Map.Entry<Integer, Double> entry : fromPreference.getSubjectivelyBestCompanies().entrySet()) {
				toPreference.updateCompanyAppeal(entry.getKey(), entry.getValue());
			}
		}

		return super.receiveMessage(m, from);
	}

	@Override
	protected void transferDone(Connection con) {
		// don't leave a copy for the sender
		this.deleteMessage(con.getMessage().getId(), false);
	}

	@Override
	public EpidemicRouter replicate() {
		return new EpidemicRouter(this);
	}
}
