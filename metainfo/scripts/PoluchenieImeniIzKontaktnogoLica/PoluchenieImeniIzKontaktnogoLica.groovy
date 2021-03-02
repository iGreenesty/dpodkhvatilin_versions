if(subject.clientName)
{
	def splitted = subject.clientName.split('\\s')
	if(splitted.size() > 1)
		return splitted[1]
}
return ''