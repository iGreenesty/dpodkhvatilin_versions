// Если заполнено имя контактного лица, то обращаемся по имени, иначе - "клиент"
notification.scriptParams['greeting'] = subject.clientName ? "Уважаемый(ая) ${subject.clientName}" : "Уважаемый клиент"

//Описание заявки, если пустое то выводим тему
notification.scriptParams['description'] = subject.descriptionRTF ?: "[не указано]";

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''

//Добавляет контактный e-mail в список получателей оповещения.
 //ПАРАМЕТРЫ----------------------------------------------------------
def CLIENT_EMAIL = 'clientEmail' // Код атрибута "Контактный е-мэйл"
def CLIENT_NAME = 'clientName' // Код атрибута "Контактное лицо"
 //ОСНОВНОЙ БЛОК------------------------------------------------------
def clientEmail = subject[CLIENT_EMAIL]
notification.to[clientEmail] = subject[CLIENT_NAME]