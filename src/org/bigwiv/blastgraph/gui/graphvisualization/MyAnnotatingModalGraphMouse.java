package org.bigwiv.blastgraph.gui.graphvisualization;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.ItemSelectable;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import edu.uci.ics.jung.visualization.MultiLayerTransformer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.annotations.AnnotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ShearingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;

public class MyAnnotatingModalGraphMouse<V, E> extends AbstractModalGraphMouse
		implements ModalGraphMouse, ItemSelectable {
	protected AnnotatingGraphMousePlugin<V, E> annotatingPlugin;
	protected MultiLayerTransformer basicTransformer;
	protected RenderContext<V, E> rc;
	
	private MyPickingGraphMousePlugin<V, E> myPickingPlugin;
	private MyPickingPopupGraphMousePlugin<V, E> myPickingPopupPlugin;
	
	/**
	 * create an instance with default values
	 * 
	 */
	public MyAnnotatingModalGraphMouse(RenderContext<V, E> rc,
			AnnotatingGraphMousePlugin<V, E> annotatingPlugin) {
		this(rc, annotatingPlugin, 1.1f, 1 / 1.1f);
	}

	/**
	 * create an instance with passed values
	 * 
	 * @param in
	 *            override value for scale in
	 * @param out
	 *            override value for scale out
	 */
	public MyAnnotatingModalGraphMouse(RenderContext<V, E> rc,
			AnnotatingGraphMousePlugin<V, E> annotatingPlugin, float in,
			float out) {
		super(in, out);
		this.rc = rc;
		this.basicTransformer = rc.getMultiLayerTransformer();
		this.annotatingPlugin = annotatingPlugin;
		this.myPickingPlugin = new MyPickingGraphMousePlugin<V, E>();
		this.myPickingPopupPlugin = new MyPickingPopupGraphMousePlugin<V, E>();
		loadPlugins();
		setModeKeyListener(new ModeKeyAdapter(this));
	}

	/**
	 * create the plugins, and load the plugins for TRANSFORMING mode
	 * 
	 */
	@Override
	protected void loadPlugins() {
		this.pickingPlugin = myPickingPlugin;
		this.animatedPickingPlugin = new AnimatedPickingGraphMousePlugin<V, E>();
		this.translatingPlugin = new TranslatingGraphMousePlugin(
				InputEvent.BUTTON1_MASK);
		this.scalingPlugin = new ScalingGraphMousePlugin(
				new CrossoverScalingControl(), 0, in, out);
		this.rotatingPlugin = new RotatingGraphMousePlugin();
		this.shearingPlugin = new ShearingGraphMousePlugin();
		add(scalingPlugin);
		setMode(Mode.TRANSFORMING);
	}

	/**
	 * setter for the Mode.
	 */
	@Override
	public void setMode(Mode mode) {
		if (this.mode != mode) {
			fireItemStateChanged(new ItemEvent(this,
					ItemEvent.ITEM_STATE_CHANGED, this.mode,
					ItemEvent.DESELECTED));
			this.mode = mode;
			if (mode == Mode.TRANSFORMING) {
				setTransformingMode();
			} else if (mode == Mode.PICKING) {
				setPickingMode();
			} else if (mode == Mode.ANNOTATING) {
				setAnnotatingMode();
			}
			if (modeBox != null) {
				modeBox.setSelectedItem(mode);
			}
			fireItemStateChanged(new ItemEvent(this,
					ItemEvent.ITEM_STATE_CHANGED, mode, ItemEvent.SELECTED));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uci.ics.jung.visualization.control.ModalGraphMouse#setPickingMode()
	 */
	@Override
	protected void setPickingMode() {
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(annotatingPlugin);
		add(pickingPlugin);
		add(animatedPickingPlugin);
		add(myPickingPopupPlugin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uci.ics.jung.visualization.control.ModalGraphMouse#setTransformingMode
	 * ()
	 */
	@Override
	protected void setTransformingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(annotatingPlugin);
		remove(myPickingPopupPlugin);
		add(translatingPlugin);
		add(rotatingPlugin);
		add(shearingPlugin);
	}

	protected void setEditingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(annotatingPlugin);
	}

	protected void setAnnotatingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(myPickingPopupPlugin);
		add(annotatingPlugin);
	}

	/**
	 * @return Returns the modeBox.
	 */
	@Override
	public JComboBox getModeComboBox() {
		if (modeBox == null) {
			modeBox = new JComboBox(new Mode[] { Mode.TRANSFORMING,
					Mode.PICKING, Mode.ANNOTATING });
			modeBox.addItemListener(getModeListener());
		}
		modeBox.setSelectedItem(mode);
		return modeBox;
	}

	public static class ModeKeyAdapter extends KeyAdapter {
		private char t = 't';
		private char p = 'p';
		private char a = 'a';
		protected ModalGraphMouse graphMouse;

		public ModeKeyAdapter(ModalGraphMouse graphMouse) {
			this.graphMouse = graphMouse;
		}

		public ModeKeyAdapter(char t, char p, char a, ModalGraphMouse graphMouse) {
			this.t = t;
			this.p = p;
			this.a = a;
			this.graphMouse = graphMouse;
		}

		@Override
		public void keyTyped(KeyEvent event) {
			char keyChar = event.getKeyChar();
			if (keyChar == t) {
				((Component) event.getSource()).setCursor(Cursor
						.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				graphMouse.setMode(Mode.TRANSFORMING);
			} else if (keyChar == p) {
				((Component) event.getSource()).setCursor(Cursor
						.getPredefinedCursor(Cursor.HAND_CURSOR));
				graphMouse.setMode(Mode.PICKING);
			} else if (keyChar == a) {
				((Component) event.getSource()).setCursor(Cursor
						.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				graphMouse.setMode(Mode.ANNOTATING);
			}
		}
	}

	public PickingGraphMousePlugin getPickingPluging() {
		return (PickingGraphMousePlugin) this.pickingPlugin;
	}

	public void addPichedChangeListener(CollectionChangeListener<V> vListener,
			CollectionChangeListener<E> eListener) {
		myPickingPlugin.addVerticesChangeListener(vListener);
		myPickingPlugin.addEdgesChangeListener(eListener);
	}

	
	
	/**
	 * create (if necessary) and return a menu that will change the mode
	 * 
	 * @return the menu
	 */
	@Override
	public JMenu getModeMenu() {
		if (modeMenu == null) {
			modeMenu = new JMenu();

			final JRadioButtonMenuItem transformingButton = new JRadioButtonMenuItem(
					Mode.TRANSFORMING.toString());
			transformingButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.TRANSFORMING);
					}
				}
			});

			final JRadioButtonMenuItem pickingButton = new JRadioButtonMenuItem(
					Mode.PICKING.toString());
			pickingButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.PICKING);
					}
				}
			});

			final JRadioButtonMenuItem annotatingButton = new JRadioButtonMenuItem(
					Mode.ANNOTATING.toString());
			annotatingButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.ANNOTATING);
					}
				}
			});

			ButtonGroup radio = new ButtonGroup();
			radio.add(transformingButton);
			radio.add(pickingButton);
			radio.add(annotatingButton);
			transformingButton.setSelected(true);
			modeMenu.add(transformingButton);
			modeMenu.add(pickingButton);
			modeMenu.add(annotatingButton);
			modeMenu.setToolTipText("Menu for setting Mouse Mode");
			addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						if (e.getItem() == Mode.TRANSFORMING) {
							transformingButton.setSelected(true);
						} else if (e.getItem() == Mode.PICKING) {
							pickingButton.setSelected(true);
						} else if (e.getItem() == Mode.ANNOTATING) {
							annotatingButton.setSelected(true);
						}
					}
				}
			});
		}
		return modeMenu;
	}
}
