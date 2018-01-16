package application.Client;

/**
 * @author Killian Nolan - R00129172 - DWEB4
 */

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import application.ListObserver;
import application.Server.MonitorInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;


public class MyMediaPlayer extends Application implements Observer{

	Button button;
	private static  Button     quit;
	Media media;
	MediaPlayer mediaPlayer;
	ListView<String> lvList, lvList2;
	ObservableList<String> items,items2, itemsDifference, serverSongs;
	ArrayList<String> list;


	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) throws IOException, MalformedURLException, RemoteException, NotBoundException{

		final String HOST = "localhost";
		final int PORT = 5555;
		Registry registry;

		registry = LocateRegistry.getRegistry(HOST, PORT);

		MonitorInterface monitorServer = (MonitorInterface) registry
				.lookup(MonitorInterface.class.getSimpleName());

		System.out.println("The client is connected with the server.");

		MonitorLocal localInstance = MonitorLocal.getInstance();
		lvList = new ListView<String>();
		lvList2 = new ListView<String>();
		list = new ArrayList<String>();
		items = FXCollections.observableArrayList (localInstance.getNames());
		items2 = FXCollections.observableArrayList (monitorServer.getNames());
		list.addAll(items2);
		items2.removeAll(items);
		lvList.setItems(items);
		lvList.getSelectionModel().select(0);
		lvList2.getItems().addAll(items2);

		media = new Media(new File("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder1/" + items.get(0)).toURI().toString());
		mediaPlayer = new MediaPlayer(media);

		Button playButton = new Button("Play");
		playButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent actionEvent) {
				if (playButton.getText().equals("Play")) {
					mediaPlayer.play();
					playButton.setText("Pause");
				} else {
					mediaPlayer.pause();
					playButton.setText("Play");
				}
			}
		});

		Button rewindButton = new Button("<<");
		rewindButton.setOnAction(e -> mediaPlayer.seek(Duration.ZERO));
		Button btnAdd = new Button("Download");

		lvList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				mediaPlayer.stop();
				mediaPlayer.dispose();
				playButton.setText("Play");
				playButton.setDisable(false);
				btnAdd.setDisable(true);
				String string = ("/Users/killiannolan/Documents/FinalYear/DistributedSysProgramming/Lab1-Sys/Folder1/" + newValue);
				try {
					media = new Media(new File(string).toURI().toString());
					mediaPlayer = new MediaPlayer(media);
				}
				catch (MediaException e) {
				}
				playButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override public void handle(ActionEvent actionEvent) {
						if (playButton.getText().equals("Play")) {
							mediaPlayer.play();
							playButton.setText("Pause");
						} else {
							mediaPlayer.pause();
							playButton.setText("Play");
						}
					}
				});
				rewindButton.setOnAction(e -> mediaPlayer.seek(Duration.ZERO));
			}
		});

		lvList2.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				mediaPlayer.stop();
				mediaPlayer.dispose();
				playButton.setText("Play");
				playButton.setDisable(true);
				btnAdd.setDisable(false);
			}
		});

		Label mediaListLabel = new Label("Home Directory");
		Label differenceLabel = new Label("Songs on Server");		

		btnAdd.setDisable(true);
		btnAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent actionEvent) {

				try {
					byte [] bytes = monitorServer.getB(lvList2.getSelectionModel().getSelectedItem());
					localInstance.copyFile(bytes, lvList2.getSelectionModel().getSelectedItem());
				} catch (RemoteException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
		//
		Button upload = new Button ("Upload");
		upload.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser chooser = new FileChooser();
				FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("select your media(*.mp3)", "*.MP3");
				chooser.getExtensionFilters().add(filter);
				File file = chooser.showOpenDialog(primaryStage);
				String file2 = file.toString();
				String songName = file.getName(); 
				File fileSong = new File(file2);
				byte [] bytesUpload = localInstance.getB(fileSong);
				try {
					monitorServer.copyFile(bytesUpload, songName);
					items = FXCollections.observableArrayList (localInstance.getNames());
					items2 = FXCollections.observableArrayList (monitorServer.getNames());
					items2.removeAll(items);
					lvList2.getItems().clear();
					lvList2.getItems().addAll(items2);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		Slider slVolume = new Slider();
		slVolume.setPrefWidth(150);
		slVolume.setMaxWidth(Region.USE_PREF_SIZE);
		slVolume.setMinWidth(30);
		slVolume.setValue(50);
		mediaPlayer.volumeProperty().bind(
				slVolume.valueProperty().divide(100));


		quit = new Button ("Quit");
		quit.setOnAction(e->System.exit(0));

		BorderPane border = new BorderPane();
		border.setPadding(new Insets(20, 20, 20, 20));

		playButton.setMaxWidth(Double.MAX_VALUE);

		VBox row1 = new VBox(20);
		row1.getChildren().addAll(mediaListLabel, lvList,differenceLabel,lvList2,btnAdd,quit);

		HBox layout = new HBox(30);
		layout.setPadding(new Insets(20, 20, 20, 20));
		layout.getChildren().addAll(row1);

		VBox col2 = new VBox(50);
		col2.getChildren().addAll(playButton,rewindButton,slVolume, upload);
		col2.setSpacing(40);
		col2.setPadding(new Insets(0, 20, 10, 20)); 
		layout.getChildren().addAll(col2);

		Scene scene = new Scene(layout, 500, 600);
		primaryStage.setTitle("Media Player"); 
		primaryStage.setScene(scene); 
		primaryStage.show();

		Task<Integer> task = new Task<Integer>() {
			@Override protected Integer call() throws Exception {
				int iterations;
				for (iterations = 0; iterations < 10000000; iterations++) {
					Platform.runLater(() -> {
						Boolean bool = localInstance.checkForChange(items);
						if (bool == true) {
							items = FXCollections.observableArrayList (localInstance.getNames());
							try {
								items2 = FXCollections.observableArrayList (monitorServer.getNames());
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							items2.removeAll(items);
							lvList2.getItems().clear();
							lvList2.getItems().addAll(items2);
							lvList2.getSelectionModel().select(0);
							lvList.getItems().clear();
							lvList.getItems().addAll(items);
							lvList.getSelectionModel().select(0);
						}
					});	
					Thread.sleep(1000);
				}
				return iterations;
			}
		};

		Task<Integer> task2 = new Task<Integer>() {
			@Override protected Integer call() throws Exception {
				int iterations;
				for (iterations = 0; iterations < 10000000; iterations++) {
					Boolean bool = monitorServer.checkForChange(list);
					Platform.runLater(() -> {
						if (bool == true) {
							try {
								items = FXCollections.observableArrayList (localInstance.getNames());
								items2 = FXCollections.observableArrayList (monitorServer.getNames());
								list.removeAll(list);
								list.addAll(items2);
								items2.removeAll(items);
								lvList2.getItems().clear();
								lvList2.getItems().addAll(items2);
								lvList2.getSelectionModel().select(0);
								String serverList = "Server";
								ListObserver ListObserver =new ListObserver(serverList);
								MyMediaPlayer media =new MyMediaPlayer();
								ListObserver.register(media);
								ListObserver.notifyObserver();
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					});	
					Thread.sleep(3000);
				}
				return iterations;
			}
		};
		Thread th = new Thread(task); 
		th.setDaemon(true); 
		th.start();
//
		Thread th2 = new Thread(task2); 
		th2.setDaemon(true); 
		th2.start();

	}

	@Override
	public void update(Observable o, Object list) {
		MonitorLocal monitorInstance = MonitorLocal.getInstance();
		items = FXCollections.observableArrayList (monitorInstance.getNames());
		Platform.runLater(new Runnable() {
			@Override public void run() {
				System.out.println("There's been a change detected on your " + list + " Songs, the list has now been updated!");
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
