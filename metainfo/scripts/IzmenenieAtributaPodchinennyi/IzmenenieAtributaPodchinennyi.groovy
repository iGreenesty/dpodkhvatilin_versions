//Если нет связи с массовой заявкой
if(subject.masterMassProblem == null)
	utils.edit(subject, ['slaved' : false])
else
  	utils.edit(subject, ['slaved' : true])