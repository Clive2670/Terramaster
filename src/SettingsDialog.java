import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class SettingsDialog extends JDialog {
	Logger log = Logger.getLogger(getClass().getName());
	private final JPanel contentPanel = new JPanel();
	private JTextField txtScenerypath;
	private JComboBox cmbScenerySource;
	private final Action action = new SwingAction();
	private JCheckBox chckbxTerrain;
	private JCheckBox chckbxObjects;
	private JCheckBox chckbxBuildings;
	private Vector<Level> levels = new Vector<Level>();
	private JComboBox<Level> cmbLogLevel;
	private Logger root;

	{
		levels.addElement(Level.ALL);
		levels.addElement(Level.FINEST);
		levels.addElement(Level.FINER);
		levels.addElement(Level.INFO);
		levels.addElement(Level.WARNING);
		levels.addElement(Level.SEVERE);
	}

	/**
	 * Create the dialog.
	 */
	public SettingsDialog() {
		setTitle("Settings");
		setModal(true);
		setBounds(100, 100, 541, 270);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 40, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 22, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
		contentPanel.setLayout(gbl_contentPanel);
		{
			{
				JLabel lblSceneryPath = new JLabel("Scenery Path :");
				GridBagConstraints gbc_lblSceneryPath = new GridBagConstraints();
				gbc_lblSceneryPath.insets = new Insets(0, 0, 5, 5);
				gbc_lblSceneryPath.anchor = GridBagConstraints.EAST;
				gbc_lblSceneryPath.gridx = 0;
				gbc_lblSceneryPath.gridy = 0;
				contentPanel.add(lblSceneryPath, gbc_lblSceneryPath);
			}
			{
				txtScenerypath = new JTextField();
				GridBagConstraints gbc_txtScenerypath = new GridBagConstraints();
				gbc_txtScenerypath.weightx = 4.0;
				gbc_txtScenerypath.insets = new Insets(0, 0, 5, 5);
				gbc_txtScenerypath.fill = GridBagConstraints.HORIZONTAL;
				gbc_txtScenerypath.gridx = 1;
				gbc_txtScenerypath.gridy = 0;
				contentPanel.add(txtScenerypath, gbc_txtScenerypath);
				txtScenerypath.setColumns(10);
			}
			txtScenerypath.setText((String) TerraMaster.props.get(TerraMasterProperties.SCENERY_PATH));
		}
		final JButton selectDirectoryButton = new JButton("...");
		selectDirectoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setCurrentDirectory(new File(txtScenerypath.getText()));

				if (fc.showOpenDialog(selectDirectoryButton) == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					fc.setCurrentDirectory(f);
					txtScenerypath.setText(f.getAbsolutePath());
				}

			}
		});
		GridBagConstraints gbc_selectDirectoryButton = new GridBagConstraints();
		gbc_selectDirectoryButton.insets = new Insets(0, 0, 5, 0);
		gbc_selectDirectoryButton.gridx = 2;
		gbc_selectDirectoryButton.gridy = 0;
		contentPanel.add(selectDirectoryButton, gbc_selectDirectoryButton);
		{
			JLabel lblScenerySource = new JLabel("Scenery Source :");
			GridBagConstraints gbc_lblScenerySource = new GridBagConstraints();
			gbc_lblScenerySource.anchor = GridBagConstraints.EAST;
			gbc_lblScenerySource.insets = new Insets(0, 0, 5, 5);
			gbc_lblScenerySource.gridx = 0;
			gbc_lblScenerySource.gridy = 1;
			contentPanel.add(lblScenerySource, gbc_lblScenerySource);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						try {
							TerraMaster.mapScenery = TerraMaster.newScnMap(txtScenerypath.getText());
							TerraMaster.frame.map.repaint();
							TerraMaster.props.setProperty(TerraMasterProperties.SCENERY_PATH, txtScenerypath.getText());
							TerraMaster.props.setProperty(TerraMasterProperties.SERVER_TYPE,
									(String) cmbScenerySource.getSelectedItem());
							TerraMaster.setTileService();
							TerraMaster.svn.setScnPath(new File(txtScenerypath.getText()));
							TerraMaster.svn.setTypes(chckbxTerrain.isSelected(), chckbxObjects.isSelected(),
									chckbxBuildings.isSelected());
							TerraMaster.props.setProperty(TerraSyncDirectoryTypes.TERRAIN.name(),
									Boolean.toString(chckbxTerrain.isSelected()));
							TerraMaster.props.setProperty(TerraSyncDirectoryTypes.OBJECTS.name(),
									Boolean.toString(chckbxObjects.isSelected()));
							TerraMaster.props.setProperty(TerraSyncDirectoryTypes.BUILDINGS.name(),
									Boolean.toString(chckbxBuildings.isSelected()));
						} catch (Exception x) {
							x.printStackTrace();
						}

					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		{
			{
				cmbScenerySource = new JComboBox();
				cmbScenerySource.setModel(new DefaultComboBoxModel(new String[] { "Terrasync (SVN)", "HTTP" }));
				cmbScenerySource.setSelectedItem(TerraMaster.props.getProperty(TerraMasterProperties.SERVER_TYPE));
				GridBagConstraints gbc_comboBox = new GridBagConstraints();
				gbc_comboBox.insets = new Insets(0, 0, 5, 5);
				gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBox.gridx = 1;
				gbc_comboBox.gridy = 1;
				contentPanel.add(cmbScenerySource, gbc_comboBox);
			}
		}
		JButton addSettingsButton = new JButton("...");
		addSettingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		GridBagConstraints gbc_addSettingsButton = new GridBagConstraints();
		gbc_addSettingsButton.insets = new Insets(0, 0, 5, 0);
		gbc_addSettingsButton.gridx = 2;
		gbc_addSettingsButton.gridy = 1;
		contentPanel.add(addSettingsButton, gbc_addSettingsButton);
		{
			JLabel lblNewLabel = new JLabel("Sync");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 2;
			contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		}
		{
			JPanel panel = new JPanel();
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.insets = new Insets(0, 0, 5, 5);
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 1;
			gbc_panel.gridy = 2;
			contentPanel.add(panel, gbc_panel);
			panel.setLayout(new GridLayout(3, 1, 0, 0));
			{
				chckbxTerrain = new JCheckBox("Terrain");
				panel.add(chckbxTerrain);
				chckbxTerrain.setSelected(Boolean
						.parseBoolean(TerraMaster.props.getProperty(TerraSyncDirectoryTypes.TERRAIN.name(), "false")));
			}
			{
				chckbxObjects = new JCheckBox("Objects");
				panel.add(chckbxObjects);
				chckbxObjects.setSelected(Boolean
						.parseBoolean(TerraMaster.props.getProperty(TerraSyncDirectoryTypes.OBJECTS.name(), "false")));
			}
			{
				chckbxBuildings = new JCheckBox("Buildings");
				panel.add(chckbxBuildings);
				chckbxBuildings.setSelected(Boolean.parseBoolean(
						TerraMaster.props.getProperty(TerraSyncDirectoryTypes.BUILDINGS.name(), "false")));
			}
		}
		{
			JLabel lblLogLevel = new JLabel("Log Level");
			GridBagConstraints gbc_lblLogLevel = new GridBagConstraints();
			gbc_lblLogLevel.anchor = GridBagConstraints.EAST;
			gbc_lblLogLevel.insets = new Insets(0, 0, 0, 5);
			gbc_lblLogLevel.gridx = 0;
			gbc_lblLogLevel.gridy = 3;
			contentPanel.add(lblLogLevel, gbc_lblLogLevel);
		}
		{
			cmbLogLevel = new JComboBox<Level>();
			cmbLogLevel.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					if(root != null)
					  root.setLevel(cmbLogLevel.getItemAt(cmbLogLevel.getSelectedIndex()));
				}
			});
			cmbLogLevel.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					Level newLevell = cmbLogLevel.getItemAt(cmbLogLevel.getSelectedIndex());
					root.setLevel(newLevell);
					LogManager manager = LogManager.getLogManager();
					Enumeration<String> loggers = manager.getLoggerNames();
					while (loggers.hasMoreElements()) {
						String logger = (String) loggers.nextElement();
						Logger logger2 = manager.getLogger(logger);
						if(logger2.getLevel()!=null)
						{
							logger2.setLevel(newLevell);
						}
					}
				}
			});
			cmbLogLevel.setModel(new DefaultComboBoxModel(levels));
			GridBagConstraints gbc_cmbLogLevel = new GridBagConstraints();
			gbc_cmbLogLevel.insets = new Insets(0, 0, 0, 5);
			gbc_cmbLogLevel.fill = GridBagConstraints.HORIZONTAL;
			gbc_cmbLogLevel.gridx = 1;
			gbc_cmbLogLevel.gridy = 3;
			contentPanel.add(cmbLogLevel, gbc_cmbLogLevel);
		}
		restoreValues();
	}

	private void restoreValues() {
		root = log;
		while (root.getParent() != null)
			root = root.getParent();

		cmbLogLevel.setSelectedItem(root.getLevel());
	}

	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Terrain");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}

		public void actionPerformed(ActionEvent e) {
			if (chckbxObjects.isSelected())
				chckbxTerrain.setSelected(true);
		}
	}
}
