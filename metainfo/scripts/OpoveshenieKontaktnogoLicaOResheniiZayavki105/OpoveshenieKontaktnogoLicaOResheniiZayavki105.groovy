def createParams(action) {
  def result = []
  result << action
  result.addAll(subject.UUID.tokenize('$'))
  return result.join(',')
}

// Если заполнено имя контактного лица, то обращаемся по имени, иначе - "клиент"
notification.scriptParams['greeting'] = subject.clientName ? "Уважаемый(ая) ${subject.clientName}" : "Уважаемый клиент"

// Формирование ссылок на закрытие и возобновление заявки
def userUuid = subject.clientEmployee.UUID
def ak = api.auth.getAccessKeyByUUID(userUuid)
def forCloseURL = api.rest.editWithRedirect('custom', subject, ['state' : 'closed'], null, null, null, 'modules.restModule.getCustomHtml', "\'${createParams('closed')}\'", ak)
def forResumeURL = api.rest.editWithRedirect('custom', subject, ['state' : 'resumed'], null, null, null, 'modules.restModule.getCustomHtml', "\'${createParams('resumed')}\'", ak)
notification.scriptParams['actions'] = "<div>Вы можете ${api.web.asLink(forCloseURL, "закрыть заявку без оценки")} или ${api.web.asLink(forResumeURL, "возобновить работу по заявке")}.</div>"

// Подпись к оповещению контактного лица по email
notification.scriptParams['emailSignature'] = utils.get('root', [:]).emailSignature ?: ''

// Устанавливает оценку для заявки (нужна для оптимизации текста оповещения по размеру)
notification.scriptParams['setMark'] = {
	mark ->
	api.rest.editWithRedirect('custom', subject, ['mark': mark], null, null, null, 'modules.restModule.getCustomHtml', "\'${createParams(mark)}\'", ak)
}