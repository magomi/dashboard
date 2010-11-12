package nl.topicus.onderwijs.dashboard.web.pages;

import java.util.ArrayList;

import nl.topicus.onderwijs.dashboard.datasources.AverageRequestTime;
import nl.topicus.onderwijs.dashboard.datasources.Commits;
import nl.topicus.onderwijs.dashboard.datasources.Events;
import nl.topicus.onderwijs.dashboard.datasources.Issues;
import nl.topicus.onderwijs.dashboard.datasources.NumberOfUsers;
import nl.topicus.onderwijs.dashboard.datasources.RequestsPerMinute;
import nl.topicus.onderwijs.dashboard.datasources.Trains;
import nl.topicus.onderwijs.dashboard.keys.Location;
import nl.topicus.onderwijs.dashboard.keys.Misc;
import nl.topicus.onderwijs.dashboard.keys.Summary;
import nl.topicus.onderwijs.dashboard.modules.DataSource;
import nl.topicus.onderwijs.dashboard.web.DashboardWebSession;
import nl.topicus.onderwijs.dashboard.web.WicketApplication;
import nl.topicus.onderwijs.dashboard.web.components.alerts.AlertsPanel;
import nl.topicus.onderwijs.dashboard.web.components.bargraph.BarGraphPanel;
import nl.topicus.onderwijs.dashboard.web.components.events.EventsPanel;
import nl.topicus.onderwijs.dashboard.web.components.statustable.StatusTablePanel;
import nl.topicus.onderwijs.dashboard.web.components.table.TablePanel;
import nl.topicus.onderwijs.dashboard.web.components.twitter.TwitterPanel;
import nl.topicus.onderwijs.dashboard.web.components.weather.WeatherPanel;
import nl.topicus.onderwijs.dashboard.web.resources.ResourceLocator;

import org.apache.wicket.Application;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.odlabs.wiquery.core.commons.IWiQueryPlugin;
import org.odlabs.wiquery.core.commons.WiQueryResourceManager;
import org.odlabs.wiquery.core.javascript.JsQuery;
import org.odlabs.wiquery.core.javascript.JsStatement;

public class DashboardPage extends WebPage implements IWiQueryPlugin {

	private static final long serialVersionUID = 1L;

	public DashboardPage(final PageParameters parameters) {
		AjaxLink<Void> liveToRandomModeSwitch = new AjaxLink<Void>("live") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				((DashboardWebSession) getSession()).switchMode();
				target.addComponent(this);
			}

			@Override
			public boolean isVisible() {
				return Application.DEVELOPMENT.equals(getApplication()
						.getConfigurationType());
			}
		};
		add(liveToRandomModeSwitch);
		liveToRandomModeSwitch.add(new Label("label",
				new PropertyModel<String>(getSession(), "mode")));
		ArrayList<Class<? extends DataSource<? extends Number>>> datasources = new ArrayList<Class<? extends DataSource<? extends Number>>>();
		datasources.add(NumberOfUsers.class);
		datasources.add(AverageRequestTime.class);
		datasources.add(RequestsPerMinute.class);
		add(new BarGraphPanel("bargraph",
				new ListModel<Class<? extends DataSource<? extends Number>>>(
						datasources)));
		add(new StatusTablePanel("table"));
		add(new TablePanel("ns", Trains.class, WicketApplication.get()
				.getRepository().getKeys(Location.class).get(0), true));
		add(new TablePanel("commits", Commits.class, Summary.get(), false));
		add(new WeatherPanel("weather", WicketApplication.get().getRepository()
				.getKeys(Location.class).get(0)));
		add(new TablePanel("issues", Issues.class, Summary.get(), false));
		add(new EventsPanel("events", Events.class, Summary.get()));
		add(new AlertsPanel("alerts"));
		add(new TwitterPanel("twitter", WicketApplication.get().getRepository()
				.getKeys(Misc.class).get(0)));
	}

	@Override
	public void contribute(WiQueryResourceManager manager) {
		manager.addJavaScriptResource(ResourceLocator.class,
				"jquery.timers-1.1.3.js");
		manager.addJavaScriptResource(DashboardPage.class,
				"jquery.dashboardclock.js");
	}

	@Override
	public JsStatement statement() {
		return new JsQuery(this).$().chain("dashboardClock",
				"'resources/application/starttime'");
	}
}
