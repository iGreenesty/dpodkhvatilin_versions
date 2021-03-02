def sc = cardObject
if(!sc.clientEmployee?.email && !sc.clientEmail) {
  // если нет emailа у контргента и нет контактного emailа
  result.showMessage('Ошибка', "Не указан контактный email и email у контрагента")
}

if(subjects.size() == 0) {
  result.showMessage('Ошибка', 'Ни один файл не выбран для отправки')
}
  
def message = api.mail.sender.createMail() //создание объекта почтового сообщения

if(sc.clientEmployee != null && sc.clientEmployee.email != null) {
  message.addTo(sc.clientEmployee.title, sc.clientEmployee.email)
}

if(sc.clientEmail != sc.clientEmployee?.email) {
  message.addTo(sc.clientName, sc.clientEmail)
}

message.setSubject("В рамках заявки №${sc.number} Вам отправлены файлы") //установка темы сообщения 
message.setText("В рамках заявки №${sc.number} Вам отправлены файлы (см. вложение).") //установка текста сообщения
message.addText("<br/><br/>--<br/>${utils.get('root', [:]).emailSignature ?: ''}") //дописывает текст в конец тела сообщения

message.contentType = 'text/html' //задает тип содержимого письма

def files = subjects
files.each {
  def source = utils.getFileDataSource(it) // получаем источник данных для объекта file
  message.attachFile(source) // прикрепляем файл к сообщению
}

api.mail.sender.sendMail(message) //отправка созданного сообщения
result.showMessage('Успех', 'Оповещение отправлено')

result.reload(false)