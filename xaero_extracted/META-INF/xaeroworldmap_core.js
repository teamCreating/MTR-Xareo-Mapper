var Opcodes=Java.type('org.objectweb.asm.Opcodes')
var InsnList=Java.type('org.objectweb.asm.tree.InsnList')
var VarInsnNode=Java.type('org.objectweb.asm.tree.VarInsnNode')
var MethodInsnNode=Java.type('org.objectweb.asm.tree.MethodInsnNode')
var MethodNode=Java.type('org.objectweb.asm.tree.MethodNode')
var InsnNode=Java.type('org.objectweb.asm.tree.InsnNode')
var FieldInsnNode=Java.type('org.objectweb.asm.tree.FieldInsnNode')
var LabelNode=Java.type('org.objectweb.asm.tree.LabelNode')
var LocalVariableNode=Java.type('org.objectweb.asm.tree.LocalVariableNode')
var Label=Java.type('org.objectweb.asm.Label')
var JumpInsnNode=Java.type('org.objectweb.asm.tree.JumpInsnNode')
var FieldNode=Java.type('org.objectweb.asm.tree.FieldNode')

function clientPacketRedirectTransformCustomPatch(methodNode, patchList){
	var instructions = methodNode.instructions
	for(var i = 0; i < instructions.size(); i++) {
		var insn = instructions.get(i);
		if(insn.getOpcode() == Opcodes.INVOKESTATIC) {
			if(insn.owner.equals("net/minecraft/network/protocol/PacketUtils") && (insn.name.equals("ensureRunningOnSameThread") || insn.name.equals("m_131363_"))) {
				instructions.insert(insn, patchList);
				break;
			}
		}
	}
}

function clientPacketRedirectTransformCustom(methodNode, methodInsnNode, localVariable){
	var patchList = new InsnList()
	patchList.add(new VarInsnNode(Opcodes.ALOAD, localVariable))
	//INVOKESTATIC xaero/common/core/LaunchPlugin.chunkUpdateCallback (Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$ChunkRender;)V
	patchList.add(methodInsnNode)
	clientPacketRedirectTransformCustomPatch(methodNode, patchList)
}

function clientPacketRedirectTransformCustomDouble(methodNode, methodInsnNode, localVariable, localVariable2){
	var patchList = new InsnList()
	patchList.add(new VarInsnNode(Opcodes.ALOAD, localVariable))
	patchList.add(new VarInsnNode(Opcodes.ALOAD, localVariable2))
	patchList.add(methodInsnNode)
	clientPacketRedirectTransformCustomPatch(methodNode, patchList)
}

function clientPacketRedirectTransform(methodNode, methodInsnNode){
	clientPacketRedirectTransformCustom(methodNode, methodInsnNode, 1)
}

function addCustomGetter3(classNode, fieldName, fieldDesc, methodName, methodDesc, staticField){
	var methods = classNode.methods
	var getterNode = new MethodNode(Opcodes.ACC_PUBLIC, methodName, "()" + methodDesc, null, null)
	var labelNode1 = new LabelNode()
	var labelNode2 = new LabelNode()
	var instructions = getterNode.instructions
	instructions.add(labelNode1)
	if(!staticField)
		instructions.add(new VarInsnNode(Opcodes.ALOAD, 0))
	instructions.add(new FieldInsnNode(staticField ? Opcodes.GETSTATIC : Opcodes.GETFIELD, classNode.name, fieldName, fieldDesc))
	if(fieldDesc === "J")
		instructions.add(new InsnNode(Opcodes.LRETURN))
	else if(fieldDesc === "I")
		instructions.add(new InsnNode(Opcodes.IRETURN))
	else if(fieldDesc === "F")
		instructions.add(new InsnNode(Opcodes.FRETURN))
	else if(fieldDesc === "D")
		instructions.add(new InsnNode(Opcodes.DRETURN))
	else
		instructions.add(new InsnNode(Opcodes.ARETURN))
	instructions.add(labelNode2)
	getterNode.localVariables.add(new LocalVariableNode("this", "L" + classNode.name + ";", null, labelNode1, labelNode2, 0))
	getterNode.maxStack = 1
	getterNode.maxLocals = 1
	methods.add(getterNode)
}

function addCustomGetter2(classNode, fieldName, fieldDesc, methodName, staticField){
	addCustomGetter3(classNode, fieldName, fieldDesc, methodName, fieldDesc, staticField)
}

function addCustomGetter(classNode, fieldName, fieldDesc, methodName){
	addCustomGetter2(classNode, fieldName, fieldDesc, methodName, false)
}

function addGetter(classNode, fieldName, fieldDesc){
	addCustomGetter(classNode, fieldName, fieldDesc, "get" + (fieldName.charAt(0) + "").toUpperCase() + fieldName.substring(1))
}

function addSetter(classNode, fieldName, fieldDesc){
	var methods = classNode.methods
	var setterNode = new MethodNode(Opcodes.ACC_PUBLIC, "set" + (fieldName.charAt(0) + "").toUpperCase() + fieldName.substring(1), "(" + fieldDesc +  ")V", null, null)
	var labelNode1 = new LabelNode()
	var labelNode2 = new LabelNode()
	var instructions = setterNode.instructions
	instructions.add(labelNode1)
	instructions.add(new VarInsnNode(Opcodes.ALOAD, 0))
	instructions.add(new VarInsnNode(Opcodes.ALOAD, 1))
	instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, classNode.name, fieldName, fieldDesc))
	instructions.add(new InsnNode(Opcodes.RETURN))
	instructions.add(labelNode2)
	setterNode.localVariables.add(new LocalVariableNode("this", "L" + classNode.name + ";", null, labelNode1, labelNode2, 0))
	setterNode.localVariables.add(new LocalVariableNode("value", fieldDesc, null, labelNode1, labelNode2, 1))
	setterNode.maxStack = 2
	setterNode.maxLocals = 2
	methods.add(setterNode)
}

function insertBeforeReturn2(methodNode, patchListGetter){
	var instructions = methodNode.instructions
	for(var i = 0; i < instructions.size(); i++) {
		var insn = instructions.get(i);
		if(insn.getOpcode() >= 172 && insn.getOpcode() <= 177){
		    var toInsert = patchListGetter()
        	var patchSize = toInsert.size()
			instructions.insertBefore(insn, toInsert);
			i += patchSize
		}
	}
}

function insertBeforeReturn(methodNode, patchList){
    var patchListGetter = function() {return patchList}
    insertBeforeReturn2(methodNode, patchListGetter)
}

function insertOnInvoke2(methodNode, patchListGetter, before, invokeOwner, invokeName, invokeNameObf, invokeDesc, firstOnly){
	var instructions = methodNode.instructions
	var isObfuscated = false
	for(var i = 0; i < instructions.size(); i++) {
		var insn = instructions.get(i);
		if(insn.getOpcode() >= 182 && insn.getOpcode() <= 185) {
			if(insn.owner.equals(invokeOwner) && (insn.name.equals(invokeName) || insn.name.equals(invokeNameObf)) && insn.desc.equals(invokeDesc)) {
				if(insn.name.equals(invokeNameObf))
				    isObfuscated = true
				var toInsert = patchListGetter()
				var patchSize = toInsert.size()
				if(before)
				    instructions.insertBefore(insn, toInsert);
                else
                	instructions.insert(insn, toInsert);
                i += patchSize
                if(firstOnly)
				    break
			}
		}
	}
	return isObfuscated
}

function insertOnInvoke(methodNode, patchList, before, invokeOwner, invokeName, invokeNameObf, invokeDesc){
    var patchListGetter = function() {return patchList}
    return insertOnInvoke2(methodNode, patchListGetter, before, invokeOwner, invokeName, invokeNameObf, invokeDesc, true)
}

function initializeCoreMod() {
	return {
	}
}