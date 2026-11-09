import { useState, useEffect } from "react";
import { Alert, FlatList, StyleSheet, View } from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import * as Location from "expo-location";
import * as SQLite from "expo-sqlite";
import {
  Appbar,
  Button,
  List,
  PaperProvider,
  Switch,
  Text,
  MD3LightTheme as DefaultTheme,
} from "react-native-paper";
import myColors from "./assets/colors.json";
import myColorsDark from "./assets/colorsDark.json";

const DARK_MODE_KEY = "darkMode";

// Abre (ou reaproveita) a conexão com o banco SQLite e garante que a tabela exista.
let dbPromise = null;
function getDb() {
  if (!dbPromise) {
    dbPromise = (async () => {
      const db = await SQLite.openDatabaseAsync("mylocation.db");
      await db.execAsync(`
        CREATE TABLE IF NOT EXISTS locations (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          latitude REAL NOT NULL,
          longitude REAL NOT NULL
        );
      `);
      return db;
    })();
  }
  return dbPromise;
}

export default function App() {
  const [isSwitchOn, setIsSwitchOn] = useState(false); // variável para controle do darkMode
  const [isLoading, setIsLoading] = useState(false); // variável para controle do loading do button
  const [locations, setLocations] = useState([]); // variável para armazenar as localizações

  // Carrega tema default da lib RN PAPER com customização das cores. Para customizar o tema, veja:
  // https://callstack.github.io/react-native-paper/docs/guides/theming/#creating-dynamic-theme-colors
  const [theme, setTheme] = useState({
    ...DefaultTheme,
    myOwnProperty: true,
    colors: myColors.colors,
  });

  // load darkMode from AsyncStorage
  async function loadDarkMode() {
    try {
      const saved = await AsyncStorage.getItem(DARK_MODE_KEY);
      if (saved !== null) {
        setIsSwitchOn(JSON.parse(saved));
      }
    } catch (e) {
      console.error("Erro ao carregar o dark mode:", e);
    }
  }

  // darkMode switch event
  async function onToggleSwitch() {
    const newValue = !isSwitchOn;
    setIsSwitchOn(newValue);
    try {
      await AsyncStorage.setItem(DARK_MODE_KEY, JSON.stringify(newValue));
    } catch (e) {
      console.error("Erro ao salvar o dark mode:", e);
    }
  }

  // get location (bottao capturar localização)
  async function getLocation() {
    setIsLoading(true);

    try {
      // 1) Solicita a permissão de localização ao usuário
      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== "granted") {
        Alert.alert(
          "Permissão negada",
          "Para capturar a localização é necessário permitir o acesso à localização do dispositivo."
        );
        return;
      }

      // 2) Captura a localização real do dispositivo
      const position = await Location.getCurrentPositionAsync({
        accuracy: Location.Accuracy.Balanced,
      });
      const coords = {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
      };

      // 3) Insere no banco SQLite e atualiza a lista em tela
      const db = await getDb();
      await db.runAsync(
        "INSERT INTO locations (latitude, longitude) VALUES (?, ?);",
        coords.latitude,
        coords.longitude
      );
      await loadLocations();
    } catch (e) {
      console.error("Erro ao capturar a localização:", e);
      Alert.alert("Erro", "Não foi possível capturar a localização.");
    } finally {
      setIsLoading(false);
    }
  }

  // load locations from db sqlite - faz a leitura das localizações salvas no banco de dados
  async function loadLocations() {
    setIsLoading(true);

    try {
      const db = await getDb();
      const rows = await db.getAllAsync(
        "SELECT id, latitude, longitude FROM locations ORDER BY id DESC;"
      );
      setLocations(rows);
    } catch (e) {
      console.error("Erro ao carregar as localizações:", e);
    } finally {
      setIsLoading(false);
    }
  }

  // Use Effect para carregar o darkMode e as localizações salvas no banco de dados
  // É executado apenas uma vez, quando o componente é montado
  useEffect(() => {
    loadDarkMode();
    loadLocations();
  }, []);

  // Efetiva a alteração do tema dark/light quando a variável isSwitchOn é alterada
  // É executado sempre que a variável isSwitchOn é alterada
  useEffect(() => {
    setTheme((current) => ({
      ...current,
      colors: isSwitchOn ? myColorsDark.colors : myColors.colors,
    }));
  }, [isSwitchOn]);

  return (
    <PaperProvider theme={theme}>
      <Appbar.Header>
        <Appbar.Content title="My Location BASE" />
      </Appbar.Header>
      <View style={{ flex: 1, backgroundColor: theme.colors.background }}>
        <View style={styles.containerDarkMode}>
          <Text>Dark Mode</Text>
          <Switch value={isSwitchOn} onValueChange={onToggleSwitch} />
        </View>
        <Button
          style={styles.containerButton}
          icon="map"
          mode="contained"
          loading={isLoading}
          onPress={() => getLocation()}
        >
          Capturar localização
        </Button>

        <FlatList
          style={styles.containerList}
          data={locations}
          keyExtractor={(item) => String(item.id)}
          ListEmptyComponent={
            <Text style={styles.emptyText}>Nenhuma localização capturada.</Text>
          }
          renderItem={({ item }) => (
            <List.Item
              title={`Localização ${item.id}`}
              description={`Latitude: ${item.latitude} | Longitude: ${item.longitude}`}
            ></List.Item>
          )}
        ></FlatList>
      </View>
    </PaperProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    alignItems: "center",
    justifyContent: "center",
  },
  containerDarkMode: {
    margin: 10,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },
  containerButton: {
    margin: 10,
  },
  containerList: {
    margin: 10,
    height: "100%",
  },
  emptyText: {
    textAlign: "center",
    marginTop: 20,
  },
});
