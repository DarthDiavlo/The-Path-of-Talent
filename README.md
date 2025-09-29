# The-Path-of-Talent
Это руководство описывает процесс запуска сети Hyperledger Fabric с ERC-721 смарт-контрактом для работы с NFT токенами.

## Запуск сети

Для запуска сети выполните следующие команды:

```bash
./network.sh up createChannel -ca
./network.sh deployCC -ccn token_erc721 -ccp ../token-erc-721/chaincode-go/ -ccl go
```
## Создание аккаунтов
Сначала нам нужно установить следующие переменные среды

```bash
export PATH=${PWD}/../bin:${PWD}:$PATH
export FABRIC_CFG_PATH=$PWD/../config/
export FABRIC_CA_CLIENT_HOME=${PWD}/organizations/peerOrganizations/org1.example.com/
```
Регистрация
```bash
fabric-ca-client register --caname ca-org1 --id.name minter --id.secret minterpw --id.type client --tls.certfiles ${PWD}/organizations/fabric-ca/org1/tls-cert.pem
fabric-ca-client enroll -u https://minter:minterpw@localhost:7054 --caname ca-org1 -M ${PWD}/organizations/peerOrganizations/org1.example.com/users/minter@org1.example.com/msp --tls.certfiles ${PWD}/organizations/fabric-ca/org1/tls-cert.pem
cp ${PWD}/organizations/peerOrganizations/org1.example.com/msp/config.yaml ${PWD}/organizations/peerOrganizations/org1.example.com/users/minter@org1.example.com/msp/config.yaml
```
Проделаем тоже самое на org2 (необходимо открыть 2 терминал):
```bash
export PATH=${PWD}/../bin:${PWD}:$PATH
export FABRIC_CA_CLIENT_HOME=${PWD}/organizations/peerOrganizations/org2.example.com/
fabric-ca-client register --caname ca-org2 --id.name recipient --id.secret recipientpw --id.type client --tls.certfiles ${PWD}/organizations/fabric-ca/org2/tls-cert.pem
fabric-ca-client enroll -u https://recipient:recipientpw@localhost:8054 --caname ca-org2 -M ${PWD}/organizations/peerOrganizations/org2.example.com/users/recipient@org2.example.com/msp --tls.certfiles ${PWD}/organizations/fabric-ca/org2/tls-cert.pem
cp ${PWD}/organizations/peerOrganizations/org2.example.com/msp/config.yaml ${PWD}/organizations/peerOrganizations/org2.example.com/users/recipient@org2.example.com/msp/config.yaml
```
## Инициализировать контракт
После того как мы создали учётную запись майнера, мы можем инициализировать контракт. Обратите внимание, что перед использованием любых функций контракта необходимо вызвать функцию инициализации. Функция инициализации() может быть вызвана только один раз.
Вернёмся к терминалу Org1. Мы установим следующие переменные среды, чтобы использовать peer CLI в качестве идентификатора minter в Org1.
```bash
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID=Org1MSP
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/minter@org1.example.com/msp
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_ADDRESS=localhost:7051
export TARGET_TLS_OPTIONS=(-o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt" --peerAddresses localhost:9051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt")
```
вызвать смарт-контракт для его инициализации
```bash
peer chaincode invoke "${TARGET_TLS_OPTIONS[@]}" -C mychannel -n token_erc721 -c '{"function":"Initialize","Args":["some name", "some symbol"]}'
```
## Создание токена
```bash
export CORE_PEER_TLS_ENABLED=true
export CORE_PEER_LOCALMSPID=Org1MSP
export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/minter@org1.example.com/msp
export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt
export CORE_PEER_ADDRESS=localhost:7051
export TARGET_TLS_OPTIONS="-o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile ${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem --peerAddresses localhost:7051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses localhost:9051 --tlsRootCertFiles ${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt"
```
Создание невзаимозаменяемого токена с уникальным идентификатором 101:
```bash
peer chaincode invoke $TARGET_TLS_OPTIONS -C mychannel -n token_erc721 -c '{"function":"MintWithTokenURI","Args":["101", "https://example.com/nft101.json"]}'
```
К примеру проверим владельца выпущенного токена, вызвав функцию OwnerOf
```bash
peer chaincode query -C mychannel -n token_erc721 -c '{"function":"OwnerOf","Args":["101"]}'
```
Отключить сеть

```bash
./network.sh down
```



