package com.protoplant.xtruder2.panel.summary;

import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wb.swt.SWTResourceManager;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.protoplant.xtruder2.event.PanelFocusEvent;

public class BaseSummaryPanel extends Group {

	protected Logger log;
	protected EventBus eb;
	private boolean isFocused = false;

	
	public BaseSummaryPanel(Composite parent, Injector injector) {
		super(parent, SWT.BORDER);
		setTouchEnabled(true);
		setText("Base");

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				setFocus();
			}
		});
		
		if (injector!=null) injector.injectMembers(this);
	}

	@Inject
	public void inject(Logger log, EventBus eb) {
		this.log = log;
		this.eb = eb;
	}
	
	
	@Subscribe
	public void onPanelFocus(PanelFocusEvent event) {
		if (event.getWidget()==this) {
			isFocused  = true;
			setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));   //new RGB(230, 190, 190);
		} else {
			isFocused = false;
			setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		}
	}
	
	@Override
	public boolean setFocus() {
		eb.post(new PanelFocusEvent(this));
		return true;
	}
	

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
