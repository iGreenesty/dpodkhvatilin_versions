if(subject.clientName)
{
	def splitted = subject.clientName.split('\\s')
	if(splitted.size() > 2)
		return splitted[2]
}
return ''