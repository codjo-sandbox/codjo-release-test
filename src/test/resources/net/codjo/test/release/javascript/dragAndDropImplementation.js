/**
 * dragOn
 * =============================================================================
 * permet de glisser-d�poser des �l�ments html
 *
 * @author      Erwan Lef�vre <erwan.lefevre@gmail.com>
 * @copyright   Erwan Lef�vre 2009
 * @license     Creative Commons - Paternit� 2.0 France - http://creativecommons.org/licenses/by/2.0/fr/
 * @version     2.1 | 2010-08-12
 * @see			http://www.webbricks.org/bricks/dragOn/
 *
 * @compatible	au 12 ao�t 2010, compatibilit� assur�e pour :
 *				Firefox 1.5+, Internet Explorer 5+, Op�ra 10, Safari 3.2.3.
 *				Autres versions et navigateurs non test�s
 */



/* exemples

-	version basique :

	dragOn.apply(getElementById('draggable')); // permet de d�placer l'�l�ment dont l'attribut id est "draggable"


-	avec options :

	dragOn.apply ( getElementById('draggable'),
		{
			handle : document.getElementById('handle'),		// l'�l�ment par o� saisir "draggable" (la poign�e, si on veut)
			movingClass : 'isMoving',						// la classe css � ajouter � "draggable" quand on le d�place
			cssPosition : 'fixed',							// indique la position css initiale de "draggable" (utile pour 'fixed' uniquement)
			moveArea : document.getElementById('area'),		// l'�l�ment dragg� ne pourra �tre d�plac� hors de la surface de l'�l�ment 'area'
			moveHoriz : false								// l'�l�ment dragg� ne pourra pas �tre d�plac� � l'horizontale
		}
	);

*/






/**
 * addEvent()
 *
 * ajoute la fonction /fn/ � la pile de r�solution de l'�v�nement /evenType/ de
 * l'objet /obj/
 *
 * merci � : http://www.scottandrew.com/weblog/articles/cbs-events
 *
 * @param		{Mixed}				obj			window, ou document, ou un �l�ment HTML
 * @param		{String}			evType		type d'event (click, mouseover, mouseout, etc.)
 * * @param		{String}			fn			la fonction � ajouter
 * @param		{Boolean}			useCapture	"useCapture un bool�en : true pour la phase de capture, ou false pour la phase de bouillonnement et la cible. On utilise quasiment toujours la valeur false."
 *
 * @returns		void
 *
 * -----------------------------------------------------------------------------
 */
function addEvent (obj, evType, fn, useCapture){
	if (obj.addEventListener) { obj.addEventListener(evType, fn, useCapture); }
	else { obj.attachEvent("on"+evType, fn); }
}





/**
 * getPos()
 * =============================================================================
 * retourne la position (dans la page) de chacun des c�t�s de l'�l�ment /elem/,
 * dispatch� dans un tableau associatif contenant les cl�s t|b|l|r
 * (la valeur retourn�e est donn�e en pixels)
 * (tient compte des diff�rences de fonctionnement des navigateur)
 *
 * @param           Object          elem            l'�l�ment inspect�
 * @return          Integer
 * @access          public
 */
function getPos(elem) {
    var pos={'r':0,'l':0,'t':0,'b':0};
    var tmp=elem;

    // on proc�de de parent en parent car IE fonctionne comme �a
    // (les autres donnent directement la position par rapport � la page)

    do {
        pos.l += tmp.offsetLeft;
        tmp = tmp.offsetParent;
    } while( tmp !== null );
    pos.r = pos.l + elem.offsetWidth;

    tmp=elem;
    do {
        pos.t += tmp.offsetTop;
        tmp = tmp.offsetParent;
    } while( tmp !== null );
    pos.b = pos.t + elem.offsetHeight;

    return pos;
}





/**
 * mousePos()
 * =============================================================================
 * retourne un tableau {'x','y'} des positions de la souris dans la page
 *
 * @return		void
 * @param		event		e		l'�v�nement auquel on attribue cette f�
 *
 */
var mousePos = {'x':0,'y':0};
function getMousePos(e) {
	var d = document,
		de = d.documentElement,
		db = document.body;

	e = e || window.event;
	if (e.pageX || e.pageY) {
		mousePos.x = e.pageX;
		mousePos.y = e.pageY;
	}
	else if (e.clientX || e.clientY) {
		mousePos.x = e.clientX + db.scrollLeft + de.scrollLeft;
		mousePos.y = e.clientY + db.scrollTop + de.scrollTop;
	}
}
/**
 * mise en place du relev� de coordon�es de la souris, en cas de d�placement de la souris
 */
addEvent(document, 'mousemove', getMousePos);





/** inRange()
 * -----------------------------------------------------------------------------
 * retourne true si le vecteur /aMin/-/aMax/ recouvre le vecteur /bMin/-/bMax/, sinon false
 *
 * @param           Float          aMin            valeur minimale du vecteur a
 * @param           Float          aMax            valeur maximale du vecteur a
 * @param           Float          bMin            valeur minimale du vecteur b
 * @param           Float          bMax            valeur maximale du vecteur b
 *
 * @return          Boolean
 */

function inRange(aMin, aMax, bMin, bMax) {
    if ( ( (aMin<=bMax) && (aMin>=bMin) ) || ( (aMax<=bMax) && (aMax>=bMin) ) ) { return true; }
    return false ;
}






/** isOver()
 * -----------------------------------------------------------------------------
 * retourne true si l'objet /a/ recouvre au moins une partie de l'objet b, sinon false
 *
 * @param           Object          a            l'objet couvrant
 * @param           Object          b            l'objet couvert
 *
 * @return          Boolean
 */
function isOver(a, b) {
    var posA = getPos(a),
        posB = getPos(b),
        aTop, aBottom, aLeft, aRight,
        bTop, bBottom, bLeft, bRight;

    aTop    = posA.t;
    aBottom = posA.b;
    aLeft   = posA.l;
    aRight  = posA.r;
    bTop    = posB.t;
    bBottom = posB.b;
    bLeft   = posB.l;
    bRight  = posB.r;

    if ( inRange(aTop, aBottom, bTop, bBottom) && inRange(aLeft, aRight, bLeft, bRight) ) { return true; }

    return false;
}






/**
 * dragOn v2.1 | 2010-08-12
 * =============================================================================
 * permet de glisser-d�poser des �l�ments html
 *
 * @requires	getPos
 * @requires	inRange		uniquement pour les contraintes de d�placement
 * @requires	isOver		uniquement pour les contraintes de d�placement
 * @requires	mousePos
 * @requires	addEvent
 *
 */
var dragOn = {

	decalX : 0, // m�morise le d�calage horizontal entre la souris et l'�l�ment dragu�
	decalY : 0, // m�morise le d�calage vertical entre la souris et l'�l�ment dragu�
	isDragging : 0, // m�morise l'�l�ment en train d'�tre dragu�
	maxZ : 0, // z-index de l'�l�ment le plus proche (le dernier qu'on a avanc�)



	/**
	 * dragOn.before()
	 *
	 * passer un �l�ment html au premier plan (par d�faut : l'�l�ment dragu�)
	 *
	 * @param		htmlElement		elem		l'�l�ment html passer au premier plan (par d�faut : l'�l�ment dragu�)
	 * @returns		void
	 * -------------------------------------------------------------------------
	 */
	before : function (elem) {
		elem = elem || this.isDragging;
		this.maxZ ++ ;
		elem.style.zIndex = this.maxZ;

		// gestion des contraintes min/max par zone-�l�ment
		if (elem.dragOptions.moveArea) {
			var area = typeof elem.dragOptions.moveArea=='object' ? elem.dragOptions.moveArea : elem.dragOptions.moveArea.parentNode;
			area = getPos(area);
			elem.dragOptions.minX = area.l;
			elem.dragOptions.maxX = area.r;
			elem.dragOptions.minY = area.t;
			elem.dragOptions.maxY = area.b;
		}
	},



	/**
	 * dragOn.start()
	 *
	 * activer un dragging
	 *
	 * @param		htmlElement		elem		l'�l�ment html � draguer
	 * @returns		void
	 * -------------------------------------------------------------------------
	 */
	start : function (elem) {
		// gestion des contraintes min/max par zone-�l�ment
		if (elem.dragOptions.moveArea) {
			var area = typeof elem.dragOptions.moveArea=='object' ? elem.dragOptions.moveArea : elem.dragOptions.moveArea.parentNode;
			area = getPos(area);
			elem.dragOptions.minX = area.l;
			elem.dragOptions.maxX = area.r;
			elem.dragOptions.minY = area.t;
			elem.dragOptions.maxY = area.b;
		}

		// relev� de l'�l�ment dragu�
		this.isDragging = elem;

		// relev� initial de la position de l'�l�ment draggu�
		elem.style.top=getPos(elem).t+'px';
		elem.style.left=getPos(elem).l+'px';

		// s'il a une position autre que fixed, la passer en absolute
		if (elem.dragOptions.cssPosition!=='fixed') { elem.style.position='absolute'; }

		// si d�finie en options, donner une classe css � l'�l�ment
		if (elem.dragOptions.movingClass) { elem.className+=" "+elem.dragOptions.movingClass; }

		// calcul de l'�cart avec le curseur
		dragOn.decalX = mousePos.x - getPos(elem).l;
		dragOn.decalY = mousePos.y - getPos(elem).t;

		// gestion du zindex
		this.before(elem); // seul ie6 me force � indiquer elem ici
		this.move();
	},



	/**
	 * dragOn.stop()
	 *
	 * arr�ter un dragging (correspond � un drop non cibl�)
	 *
	 * @returns		void
	 * -------------------------------------------------------------------------
	 */
	stop : function () {
		var elem = dragOn.isDragging;
		if (elem) {

			// si d�finie en options, retirer la classe css de dragage
			if (elem.dragOptions.movingClass) { elem.className = elem.className.replace(" "+elem.dragOptions.movingClass,''); }

			// ne plus consid�rer d'�l�ment � draguer
			dragOn.isDragging = 0;
		}
	},



	/**
	 * dragOn.apply()
	 *
	 * appliquer une capacit� de draguage � un �l�ment html
	 *
	 * @param		htmlElement		target		l'�l�ment html � draguer
	 * @param		object			otions		liste des options pour l'�l�ment � draguer :
	 *												-	htmlElement		handle				l'�l�m�net html qui servira de poign�e (par d�faut : target)
	 *												-	boolean			cssPosition			indique la position css initiale de target (pour les position statiques en particulier)
	 *												-	string			movingClass			nom de classe css � donner � target qd on le d�place
	 * @returns		void
	 * -------------------------------------------------------------------------
	 */
	apply : function (target, options) {
		// options par d�faut
		options = options || {};
		var handle = options.handle = options.handle ? options.handle : target ;
		options.cssPosition = options.cssPosition ? options.cssPosition : target.style.position ;
		options.moveHoriz = options.moveHoriz===undefined ? 1 : options.moveHoriz;
		options.moveVert = options.moveVert===undefined ? 1 : options.moveVert;

		// m�morisation des options
		target.dragOptions = options;

		// figer la taille de l'�l�ment dragu�
		target.style.width=target.clientWidth+'px';
		target.style.height=target.clientHeight+'px';

		// sur le handle, cr�er les �v�nement de mise en route/arr�t du drag
		var on,moveFisrt;

		on = function (e) { dragOn.start(target); };
		addEvent(handle, 'mousedown', on);

		moveFisrt = function () { dragOn.before(target); };
		addEvent(target, 'mousedown', moveFisrt);

		// emp�cher la s�lection pendant le d�placement
		handle.onselectstart = function () { return false; }; // ie
		handle.onmousedown = function () { return false; }; // mozilla
	},



	/**
	 * dragOn.move()
	 *
	 * d�placement de l'�l�ment dragu�
	 *
	 * @returns		void
	 * -------------------------------------------------------------------------
	 */
	move : function () {
		var elem = dragOn.isDragging,
			opt, // raccourci de elem.dragOptions
			left, top; // positions horizontale et verticale
		if (elem) {
//			si un �l�ment � draguer est d�fini
			if (elem) { // le d�placer
				// raccourci
				opt = elem.dragOptions;

				// position horizontale
				if (opt.moveHoriz) {
					left = mousePos.x - dragOn.decalX;
					left = opt.maxX!==undefined && opt.maxX<left+elem.offsetWidth ? opt.maxX-elem.offsetWidth : left;
					left = opt.minX!==undefined && opt.minX>left ? opt.minX : left;
					elem.style.left = left + "px" ;
				}

				// position verticale
				if (opt.moveVert) {
					top = mousePos.y - dragOn.decalY;
					top = opt.maxY!==undefined && opt.maxY<top+elem.offsetHeight ? opt.maxY-elem.offsetHeight : top;
					top = opt.minY!==undefined && opt.minY>top ? opt.minY : top;
					elem.style.top = top + "px" ;
				}
			}
		}
	}
};


/* mise ne place de l'effet */
addEvent(document, 'mouseup', dragOn.stop);
addEvent(document, 'mousemove', dragOn.move);