//Автор: dshirykalov
// SCRIPTSD4000890
//Дата создания: 22.04.2015
//Назначение: действие при входе в статус
/**
 * Ответственным за заявку назначает ответственного за услугу заявки.
 * Если услуга или ответственный за услугу не указаны,
 * назначается "глобальный" ответственный по умолчанию (из свойств компании).
 */

//ПАРАМЕТРЫ------------------------------------------------------------
def SERVICE_RESP_ATTR = 'responsible' // код атрибута "Ответственный" услуги
def SERVICE_RESP_EMPL_ATTR = 'responsibleEmployee' // код атрибута "Ответственный (сотрудник)" услуги
def SERVICE_RESP_TEAM_ATTR = 'responsibleTeam' // код атрибута "Ответственный (команда)" услуги


def AGREEMENT_RESP_ATTR = 'resp' // код атрибута "Ответственный" соглашения
def AGREEMENT_RESP_EMPL_ATTR = 'resp_em' // код атрибута "Ответственный (сотрудник)" соглашения
def AGREEMENT_RESP_TEAM_ATTR = 'resp_te' // код атрибута "Ответственный (команда)" соглашения

def GLOBAL_RESP_ATTR = 'responsDef' // код атрибута "Ответственный за заявки (по умолчанию)" компании
def GLOBAL_RESP_EMPL_ATTR = 'responsDef_em' // код атрибута "Ответственный за заявки (по умолчанию) (Сотрудник)" компании
def GLOBAL_RESP_TEAM_ATTR = 'responsDef_te' // код атрибута "Ответственный за заявки (по умолчанию) (Команда)" компании

def NOT_LICENSED_CODE = 'notLicensed' //код лицензии "Нелицензированный"

//ОСНОВНОЙ БЛОК------------------------------------------------
if ((subject.responsible != null) || (subject.masterMassProblem != null)) {
	// если ответственный за заявку был установлен на форме добавления,
	// или заявка является подчинённой, завершаем работу
	return
}

def responsibleEmployee
def responsibleTeam

if ((subject.service != null) && (subject.service[SERVICE_RESP_ATTR] != null)) {
    // если для заявки указана услуга, и для этой услуги указан ответственный, то
    // устанавливаем данного ответственного ответственным за заявку
    responsibleEmployee = subject.service[SERVICE_RESP_EMPL_ATTR]
    responsibleTeam = subject.service[SERVICE_RESP_TEAM_ATTR]        
} else if ((subject.agreement != null) && (subject.agreement[AGREEMENT_RESP_ATTR] != null)){
 	responsibleEmployee = subject.agreement[AGREEMENT_RESP_EMPL_ATTR]
    responsibleTeam = subject.agreement[AGREEMENT_RESP_TEAM_ATTR]  
} else {
	// иначе устанавливаем ответственного за заявку из "глобальных" настроек
	def root = utils.get('root', [:])
	if(root[GLOBAL_RESP_EMPL_ATTR] && root[GLOBAL_RESP_EMPL_ATTR].license != NOT_LICENSED_CODE) {
      responsibleEmployee = root[GLOBAL_RESP_EMPL_ATTR]
    }
	responsibleTeam = root[GLOBAL_RESP_TEAM_ATTR]
}

utils.edit(subject, ['responsibleEmployee': responsibleEmployee, 'responsibleTeam': responsibleTeam])