# The Path of Talent — Installation & Usage Guide


---

## 📦 Необходимые инструменты

### Установить Git
```bash
sudo apt-get install git
```

### Установить cURL
```bash
sudo apt-get install curl
```

### Установить Docker и Docker Compose
```bash
sudo apt-get -y install docker-compose
```

После установки убедитесь, что версии актуальны:
```bash
docker --version
docker-compose --version
```

Убедитесь, что демон Docker запущен:
```bash
sudo systemctl start docker
```

Добавьте своего пользователя в группу Docker:
```bash
sudo usermod -a -G docker <имя пользователя>
```

---

## 🟦 Установка Go

Установите последнюю версию Go с официального сайта:  
https://go.dev/doc/install

Получить установочный скрипт Hyperledger Fabric:
```bash
curl -sSLO https://raw.githubusercontent.com/hyperledger/fabric/main/scripts/install-fabric.sh && chmod +x install-fabric.sh
```

Установить бинарники Fabric:
```bash
./install-fabric.sh d b
```

---

## 🧬 Клонирование репозитория

```bash
git clone https://github.com/DarthDiavlo/The-Path-of-Talent.git
```

---

## 🚀 Поднятие сети Fabric

Перейти в директорию:
```bash
cd ~/The-Path-of-Talent/network
```

Запуск сети:
```bash
./network.sh up createChannel -ca
```

Деплой смарт‑контракта ERC‑721:
```bash
./network.sh deployCC -ccn token_erc721 -ccp ../erc-721/chaincode/ -ccl go
```

---

## 🔐 Настройка окружения для взаимодействия с chaincode

```bash
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID=Org1MSP
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_ADDRESS=localhost:7051

export TARGET_TLS_OPTIONS=(-o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt" --peerAddresses localhost:9051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt")
```

Инициализация chaincode:
```bash
peer chaincode invoke "${TARGET_TLS_OPTIONS[@]}" -C mychannel -n token_erc721 -c '{"function":"Initialize","Args":["some name", "some symbol"]}'
```

---

## ☕ Установка Java и Maven

```bash
sudo apt update
sudo apt install openjdk-17-jdk -y
sudo apt install maven -y
```

---

## 📁 Подготовка сертификатов для Java‑сервиса

Необходимо скопировать файлы:

**1. key.pem**

Получить из:
```
~/The-Path-of-Talent/network/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/msp/keystore/
```

Скопировать в:
```
~/The-Path-of-Talent/nftfabric/src/main/resources/
```

Команда:
```bash
cp ~/The-Path-of-Talent/network/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/msp/keystore/key.pem ~/The-Path-of-Talent/nftfabric/src/main/resources/
```

**2. cert.pem**
```bash
cp ~/The-Path-of-Talent/network/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/signcerts/cert.pem ~/The-Path-of-Talent/nftfabric/src/main/resources/
```

**3. ca.crt**
```bash
cp ~/The-Path-of-Talent/network/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt ~/The-Path-of-Talent/nftfabric/src/main/resources/
```

---

## 🏗️ Сборка и запуск Java‑сервиса

Перейти в директорию:
```bash
cd ~/The-Path-of-Talent/nftfabric
```

Сборка:
```bash
mvn clean package
```

Запуск:
```bash
java -jar target/nftfabric-0.0.1-SNAPSHOT.jar --server.port=8081
```

---

## 📤 Пример тела запроса

⚠️ **Отсутствие любого поля сломает генерацию!**

```json
{
  "text": "ДРОздов Николай",
  "layers": [
    { "category": "Олимпиады и интеллектуальные конкурсы", "level": 0, "text": "Hello NFT" },
    { "category": "Публикации и научная деятельность", "level": 1, "text": "Blue eyes" },
    { "category": "Академическая успеваемость и гранты", "level": 0, "text": "" }
  ]
}
```





