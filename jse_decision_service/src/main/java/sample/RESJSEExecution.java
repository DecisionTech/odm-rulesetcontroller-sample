/*
 * Licensed Materials - Property of IBM
 * 5725-B69 5655-Y17 5655-Y31 5724-X98 5724-Y15 5655-V82 
 * Copyright IBM Corp. 1987, 2018. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights: 
 * Use, duplication or disclosure restricted by GSA ADP Schedule 
 * Contract with IBM Corp.
 */
package sample;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static sample.MessageCode.EMPTY_RULEAPP;
import static sample.MessageCode.RULEAPP_CLASSLOADER_RESOURCE_NOT_FOUND;
import static sample.MessageCode.RULEAPP_FILE_NOT_FOUND;
import static sample.MessageCode.RULEAPP_NOT_PROCESSED;
import static sample.MessageCode.RULEAPP_PROCESSED;
import static sample.MessageCode.RULESETS_ADDED;
import static sample.MessageCode.RULESET_ADDED;
import static sample.MessageCode.SAMPLE_ERROR_INVALID_RULESET_PATH;
import static sample.MessageCode.SAMPLE_ERROR_MISSING_RULESET_PATH;
import static sample.MessageCode.SAMPLE_RULESET_PATH;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import ilog.rules.res.model.IlrAlreadyExistException;
import ilog.rules.res.model.IlrFormatException;
import ilog.rules.res.model.IlrMutableRepository;
import ilog.rules.res.model.IlrMutableRuleAppInformation;
import ilog.rules.res.model.IlrMutableRulesetArchiveInformation;
import ilog.rules.res.model.IlrPath;
import ilog.rules.res.model.IlrRepositoryFactory;
import ilog.rules.res.model.archive.IlrArchiveException;
import ilog.rules.res.model.archive.IlrArchiveManager;
import ilog.rules.res.session.IlrJ2SESessionFactory;
import ilog.rules.res.session.IlrSessionCreationException;
import ilog.rules.res.session.IlrSessionException;
import ilog.rules.res.session.IlrSessionRequest;
import ilog.rules.res.session.IlrSessionResponse;
import ilog.rules.res.session.IlrStatelessSession;
import ilog.rules.res.session.config.IlrConfigException;
import ilog.rules.res.session.config.IlrPluginConfig;
import ilog.rules.res.session.config.IlrSessionFactoryConfig;
import loan.Borrower;
import loan.LoanRequest;
import loan.Report;


public class RESJSEExecution {

	private final MessageFormatter formatter = new MessageFormatter();

	private final IlrJ2SESessionFactory factory;

	private static final Logger LOGGER = Logger.getLogger(RESJSEExecution.class.getName());

	public RESJSEExecution() {
		this(createJ2SESessionFactory());
	}
	
	/**
	 * Execute the ruleset identified by rulesetPath and specified amount
	 * @param rulesetPath
	 * @param amount
	 * @throws IlrFormatException
	 * @throws IlrSessionCreationException
	 * @throws IlrSessionException
	 */
	public void executeRulesetWithAmount(IlrPath rulesetPath, int amount) throws IlrFormatException,
	IlrSessionCreationException,
	IlrSessionException {
		// Create a session request object
		IlrSessionRequest sessionRequest = factory.createRequest();
		sessionRequest.setRulesetPath(rulesetPath);
		// Ensure latest version of the ruleset is taken into account
		sessionRequest.setForceUptodate(true);
		// Set the input parameters for the execution of the rules
		Map<String, Object> inputParameters = new HashMap<String, Object>();
		java.util.Date birthDate = loan.DateUtil.makeDate(1950, 1, 1);
		// Set borrower ruleset parameter
		loan.Borrower borrower = new Borrower("Smith", "John", birthDate, "123121234");
		borrower.setZipCode("12345");
		borrower.setCreditScore(200);
		borrower.setYearlyIncome(20000);
		// Set loan ruleset parameter
		inputParameters.put("borrower", borrower);
		loan.LoanRequest loan = new LoanRequest(new Date(), 48, amount, 1.2);
		inputParameters.put("loan", loan);
		sessionRequest.setInputParameters(inputParameters);
		// Create the stateless rule session.
		IlrStatelessSession session = factory.createStatelessSession();
		// Execute rules
		IlrSessionResponse sessionResponse = session.execute(sessionRequest);
		// Display the report
		Report report = (Report) (sessionResponse.getOutputParameters().get("report"));
		System.out.println(report.toString());
	}
	
	public void executeRuleset(String rulesetPathString, Borrower borrower, LoanRequest loan) throws IlrFormatException,
	IlrSessionCreationException,
	IlrSessionException {
		System.out.println("1 - HELLO FROM RESJSEExecution.executeRuleset" + rulesetPathString);

		// Create a session request object
		IlrSessionRequest sessionRequest = factory.createRequest();
		IlrPath rulesetPath = getRulesetPath(rulesetPathString);
		sessionRequest.setRulesetPath(rulesetPath);
		// Ensure latest version of the ruleset is taken into account
		sessionRequest.setForceUptodate(true);
		// Set the input parameters for the execution of the rules
		Map<String, Object> inputParameters = new HashMap<String, Object>();
		inputParameters.put("borrower", borrower);
		inputParameters.put("loan", loan);
		sessionRequest.setInputParameters(inputParameters);
		// Create the stateless rule session.
		IlrStatelessSession session = factory.createStatelessSession();
		// Execute rules
		System.out.println("2 - HELLO FROM RESJSEExecution.executeRuleset" + rulesetPathString);
		IlrSessionResponse sessionResponse = session.execute(sessionRequest);
		// Display the report
//		Report report = (Report) (sessionResponse.getOutputParameters().get("report"));
		System.out.println("3 - HELLO FROM RESJSEExecution.executeRuleset" + rulesetPathString);
//		System.out.println(report.toString());
	}
	
	/**
	 * 
	 * @param rulesetPathArgumentAsString
	 * @return an IlrPath constructed from provided parameter if valid
	 * @throws IllegalArgumentException
	 */
	private IlrPath getRulesetPath(String rulesetPathArgumentAsString) throws IllegalArgumentException {
		if (rulesetPathArgumentAsString == null) {
			String errorMessage = getMessage(SAMPLE_ERROR_MISSING_RULESET_PATH, getMessage(SAMPLE_RULESET_PATH));
			throw new IllegalArgumentException(errorMessage);
		}
		try {
			return IlrPath.parsePath(rulesetPathArgumentAsString);
		} catch (IlrFormatException exception) {
			String errorMessage = getMessage(SAMPLE_ERROR_INVALID_RULESET_PATH, rulesetPathArgumentAsString);
			throw new IllegalArgumentException(errorMessage);
		}
	}

	/**
	 * Execute the ruleset identified by rulesetPath with default loan amount that is 100000
	 * @param rulesetPath
	 * @throws IlrFormatException
	 * @throws IlrSessionCreationException
	 * @throws IlrSessionException
	 */
	public void executeRuleset(IlrPath rulesetPath) throws IlrFormatException,
	IlrSessionCreationException,
	IlrSessionException {
		this.executeRulesetWithAmount(rulesetPath, 100000);
	}

	/**
	 * Load a ruleapp from classpath based on provided ruleAppArchiveName
	 * @param ruleAppArchiveName
	 * @throws IlrSessionCreationException
	 * @throws IlrSessionException
	 * @throws IOException
	 * @throws IlrArchiveException
	 * @throws IlrAlreadyExistException
	 * @throws IlrFormatException
	 */
	public void loadRuleApp(String ruleAppArchiveName) throws IlrSessionCreationException,
	IlrSessionException,
	IOException,
	IlrArchiveException,
	IlrAlreadyExistException,
	IlrFormatException {
		if (ruleAppArchiveName == null) {
			return;
		}
		URL ruleAppArchiveURL = getRuleAppArchiveURL(ruleAppArchiveName);
		if (ruleAppArchiveURL != null) {
			try (InputStream inputStream = ruleAppArchiveURL.openStream()) {
				if (inputStream != null) {
					try (JarInputStream jarInputStream = new JarInputStream(inputStream)) {
						IlrArchiveManager archiveManager = new IlrArchiveManager();
						IlrRepositoryFactory repositoryFactory =
								factory.createManagementSession().getRepositoryFactory();
						IlrMutableRepository repository = repositoryFactory.createRepository();
						archiveManager.read(repositoryFactory, jarInputStream).stream()
						.forEach(new Consumer<IlrMutableRuleAppInformation>() {

							@Override
							public void accept(IlrMutableRuleAppInformation ruleApp) {
								Set<IlrPath> rulesetPaths = new HashSet<>();
								ruleApp.getRulesets().stream()
								.map(IlrMutableRulesetArchiveInformation::getCanonicalPath)
								.forEach(rulesetPaths::add);
								if (rulesetPaths.isEmpty()) {
									info(EMPTY_RULEAPP, ruleApp.getCanonicalPath());
								} else if (rulesetPaths.size() == 1) {
									info(RULESET_ADDED, rulesetPaths.stream().findFirst());
								} else {
									info(RULESETS_ADDED, rulesetPaths);
								}
								try {
									repository.addRuleApp(ruleApp);
								} catch (IlrAlreadyExistException exception) {
									// N/A
								}
							}
						});
						info(RULEAPP_PROCESSED, ruleAppArchiveName);
						return;
					}
				}
			}
		}
		throw new IllegalArgumentException(formatter.getMessage(RULEAPP_NOT_PROCESSED, ruleAppArchiveName));
	}
	
	/**
	 * @return a IlrJ2SESessionFactory session
	 */
	private static IlrJ2SESessionFactory createJ2SESessionFactory() {
//		IlrSessionFactoryConfig factoryConfig = IlrJ2SESessionFactory.createDefaultConfig();
//		IlrXUConfig xuConfig = factoryConfig.getXUConfig();
//		xuConfig.setLogAutoFlushEnabled(true);
//		xuConfig.getPersistenceConfig().setPersistenceType(MEMORY);
//		xuConfig.getManagedXOMPersistenceConfig().setPersistenceType(MEMORY);
//		return new IlrJ2SESessionFactory(factoryConfig);
		
		IlrSessionFactoryConfig cfg = IlrJ2SESessionFactory.createDefaultConfig();

		try {
			cfg.getXUConfig().loadSettings( RESJSEExecution.class.getClassLoader().getResourceAsStream("ra.xml"));
	        cfg.setXOMClassLoader(Borrower.class.getClassLoader());
	        List<IlrPluginConfig> plugins = cfg.getXUConfig().getPluginConfigs();
	        for(IlrPluginConfig plugin : plugins)
	        	if(plugin.getProperties().containsKey("xuName"))
	        		plugin.setProperty("xuName", "childXU");
	        cfg.getXUConfig().setPluginConfigs(plugins);
	        } catch (IlrConfigException e) {
	            e.printStackTrace();
	        }
	        return new IlrJ2SESessionFactory(cfg);
	}

	private RESJSEExecution(IlrJ2SESessionFactory factory) {
		this.factory = factory;
	}

	public void release() {
		factory.release();
	}

	private void info(String key, Object... arguments) {
		log(INFO, key, arguments);
	}

	private void log(Level level, String key, Object... arguments) {
		LOGGER.log(level, getMessage(key, arguments));
	}

	private String getMessage(String key, Object... arguments) {
		return formatter.getMessage(key, arguments);
	}

	private void warning(String key, Object... arguments) {
		log(WARNING, key, arguments);
	}

	/**
	 * 
	 * @param ruleAppArchiveName
	 * @return an URL of the ruleAppArchiveName if found in the classpath
	 * @throws MalformedURLException
	 */
	private URL getRuleAppArchiveURL(String ruleAppArchiveName) throws MalformedURLException {
		if (ruleAppArchiveName == null) {
			return null;
		}
		File file = new File(ruleAppArchiveName);
		if (file.exists()) {
			return file.toURI().toURL();
		}
		warning(RULEAPP_FILE_NOT_FOUND, ruleAppArchiveName);
		URL resource = this.getClass().getClassLoader().getResource(ruleAppArchiveName);
		if (resource == null) {
			warning(RULEAPP_CLASSLOADER_RESOURCE_NOT_FOUND, ruleAppArchiveName);
		}
		return resource;
	}

	
}
