using UnityEngine;
using System.Collections;

public class Intro : MonoBehaviour {

	void Start () {	
	}
	
	void Update () {
		if (Input.anyKeyDown) {
			Application.LoadLevel("Level");
		}
	}
}
