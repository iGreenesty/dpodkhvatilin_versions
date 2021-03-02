def EMPL_METACLASS = 'employee$contactPerson'
def OU_METACLASS = 'ou$company'

// Если не выбрана компания и нет названия для новой компании
if(params.addTo == null && api.string.isEmptyTrim(params.newCompanyName))
{
	utils.throwReadableException("Для создания контактного лица необходимо выбрать компанию или указать название новой компании, в которую будет помещено контактное лицо", [] as String[], "Для создания контактного лица необходимо выбрать компанию или указать название новой компании, в которую будет помещено контактное лицо", [] as String[])
}

// Если выбрана компания и указано названия для новой компании
if(params.addTo != null && !api.string.isEmptyTrim(params.newCompanyName))
{
	utils.throwReadableException("Для создания контактного лица необходимо или выбрать компанию, или указать название новой компании", [] as String[], "Для создания контактного лица необходимо или выбрать компанию, или указать название новой компании", [] as String[])
}

def company = params.addTo

//Если указано название для новой компании
if(!api.string.isEmptyTrim(params.newCompanyName))
{
  def compAttrs = [:]
  compAttrs['title'] = params.newCompanyName
  company = utils.create((OU_METACLASS), compAttrs)
}

def emplAttrs = [
	'lastName' : params.lastName,
	'firstName' : params.firstName,
	'middleName' : params.middleName,
	'parent' : company,
	'email' : params.email,
	'mobilePhoneNumber' : params.mobilePhoneNumber,
  	'recipientAgreements' : [subject.agreement]
]
def client = utils.create((EMPL_METACLASS), emplAttrs)

def scAttrs = [
	'clientEmployee' : client,
	'clientOU' : company,
	'clientName' : client.title,
	'clientEmail' : client.email,
	'clientPhone' : client.mobilePhoneNumber
]
utils.edit(subject, scAttrs)