package seedu.job;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.job.commons.core.Config;
import seedu.job.commons.core.LogsCenter;
import seedu.job.commons.core.Version;
import seedu.job.commons.exceptions.DataLoadingException;
import seedu.job.commons.util.ConfigUtil;
import seedu.job.commons.util.StringUtil;
import seedu.job.logic.Logic;
import seedu.job.logic.LogicManager;
import seedu.job.model.jobapplication.JobApplication;
import seedu.job.model.jobapplication.JobBook;
import seedu.job.model.jobapplication.Model;
import seedu.job.model.jobapplication.ModelManager;
import seedu.job.model.jobapplication.ReadOnlyJobBook;
import seedu.job.model.jobapplication.ReadOnlyUserPrefs;
import seedu.job.model.jobapplication.UserPrefs;
import seedu.job.model.jobapplication.exceptions.DuplicateJobApplicationException;
import seedu.job.storage.DataStorage;
import seedu.job.storage.DataStorageManager;
import seedu.job.storage.JobApplicationStorage;
import seedu.job.storage.JsonJobApplicationStorage;
import seedu.job.storage.JsonUserPrefsStorage;
import seedu.job.storage.UserPrefsStorage;
import seedu.job.ui.Ui;
import seedu.job.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(0, 2, 2, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected DataStorage dataStorage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing HustleHub ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());
        initLogging(config);

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);

        // New code
        JobApplicationStorage jobApplicationStorage = new JsonJobApplicationStorage(userPrefs.getJobBookFilePath());
        dataStorage = new DataStorageManager(jobApplicationStorage, userPrefsStorage);

        model = initModelManager(dataStorage, userPrefs);

        logic = new LogicManager(model, dataStorage);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s job book and {@code userPrefs}. <br>
     * The data from the sample address book will be used instead if {@code storage}'s job book is not found,
     * or an empty job book will be used instead if errors occur when reading {@code storage}'s job book.
     */
    private Model initModelManager(DataStorage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getDataFilePath());

        ReadOnlyJobBook initialData;
        JobBook book = new JobBook();

        try {

            List<JobApplication> applicationList = storage.readDataFile();
            book.setApplications(applicationList);

            logger.info("Successfully loaded " + applicationList.size() + " entries");

            initialData = book;

        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getDataFilePath() + " could not be loaded."
                    + " Will be starting with an empty JobBook.");
            initialData = book;
        } catch (DuplicateJobApplicationException e) {
            logger.warning("Data file at " + storage.getDataFilePath() + " contains duplicate job applications."
                    + " Will be starting with an empty JobBook.");
            initialData = book;
        }

        return new ModelManager(initialData, userPrefs);
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            if (!configOptional.isPresent()) {
                logger.info("Creating new config file " + configFilePathUsed);
            }
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataLoadingException e) {
            logger.warning("Config file at " + configFilePathUsed + " could not be loaded."
                    + " Using default config properties.");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage prefStorage) {
        Path prefsFilePath = prefStorage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = prefStorage.readUserPrefs();
            if (!prefsOptional.isPresent()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            prefStorage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting JobBook " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping HustleHub ] =============================");
        try {
            dataStorage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
