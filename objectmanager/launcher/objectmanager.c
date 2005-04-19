#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>

#define MAX_PATH_LENGTH   2000

#ifdef _WIN32

#ifdef UNICODE
#define _UNICODE
#endif
#include <windows.h>

#include <TCHAR.h>
#define dirSeparator '\\'
#define pathSeparator _T(';')

#else /* Platforms other than Windows */

#define dirSeparator '/'
#define pathSeparator ':'

#define _TCHAR char
#define _T(s) s
#define _tcschr strchr
#define _tcslen strlen
#define _stprintf sprintf
#define _tprintf printf
#define _tcsicmp strcasecmp
#define _tcscpy strcpy
#define _tgetcwd getcwd
#define _tcscat strcat
#define _tgetenv getenv
#define _tcsncpy strncpy
#define _tstat stat
#define _tcscmp strcmp
#define _tcsrchr strrchr
#define _stat stat

#endif /* _WIN32 */


/*
 * Find the absolute pathname to where a command resides.
 *
 * The string returned by the function must be freed.
 */
#define EXTRA 20
_TCHAR* findCommand( _TCHAR* command )
{
    _TCHAR*  cmdPath;
    int    length;
    _TCHAR*  ch;
    _TCHAR*  dir;
    _TCHAR*  path;
    struct _stat stats;

    /* If the command was an abolute pathname, use it as is. */
    if (command[0] == dirSeparator ||
       (_tcslen( command ) > 2 && command[1] == _T(':')))
    {
        length = _tcslen( command );
        cmdPath = malloc( (length + EXTRA) * sizeof(_TCHAR) ); /* add extra space for a possible ".exe" extension */
        _tcscpy( cmdPath, command );
    }

    else
    {
        /* If the command string contains a path separator */
        if (_tcschr( command, dirSeparator ) != NULL)
        {
            /* It must be relative to the current directory. */
            length = MAX_PATH_LENGTH + EXTRA + _tcslen( command );
            cmdPath = malloc( length * sizeof (_TCHAR));
            _tgetcwd( cmdPath, length );
            if (cmdPath[ _tcslen( cmdPath ) - 1 ] != dirSeparator)
            {
                length = _tcslen( cmdPath );
                cmdPath[ length ] = dirSeparator;
                cmdPath[ length+1 ] = _T('\0');
            }
            _tcscat( cmdPath, command );
        }

        /* else the command must be in the PATH somewhere */
        else
        {
            /* Get the directory PATH where executables reside. */
            path = _tgetenv( _T("PATH") );
            length = _tcslen( path ) + _tcslen( command ) + MAX_PATH_LENGTH;
            cmdPath = malloc( length * sizeof(_TCHAR));

            /* Foreach directory in the PATH */
            dir = path;
            while (dir != NULL && *dir != _T('\0'))
            {
                ch = _tcschr( dir, pathSeparator );
                if (ch == NULL)
                {
                    _tcscpy( cmdPath, dir );
                }
                else
                {
                    length = ch - dir;
                    _tcsncpy( cmdPath, dir, length );
                    cmdPath[ length ] = _T('\0');
                    ch++;
                }
                dir = ch; /* advance for the next iteration */

                /* Determine if the executable resides in this directory. */
                if (cmdPath[0] == _T('.') &&
                   (_tcslen(cmdPath) == 1 || (_tcslen(cmdPath) == 2 && cmdPath[1] == dirSeparator)))
                {
                	_tgetcwd( cmdPath, MAX_PATH_LENGTH );
                }
                if (cmdPath[ _tcslen( cmdPath ) - 1 ] != dirSeparator)
                {
                    length = _tcslen( cmdPath );
                    cmdPath[ length ] = dirSeparator;
                    cmdPath[ length+1 ] = _T('\0');
                }
                _tcscat( cmdPath, command );

                /* If the file is not a directory and can be executed */
                if (_tstat( cmdPath, &stats ) == 0 && (stats.st_mode & S_IFREG) != 0)
                {
                    /* Stop searching */
                    dir = NULL;
                }
            }
        }
    }

#ifdef _WIN32
	/* If the command does not exist */
    if (_tstat( cmdPath, &stats ) != 0 || (stats.st_mode & S_IFREG) == 0)
    {
    	/* If the command does not end with .exe, append it an try again. */
    	length = _tcslen( cmdPath );
    	if (length > 4 && _tcsicmp( &cmdPath[ length - 4 ], _T(".exe") ) != 0)
    	    _tcscat( cmdPath, _T(".exe") );
    }
#endif

    /* Verify the resulting command actually exists. */
    if (_tstat( cmdPath, &stats ) != 0 || (stats.st_mode & S_IFREG) == 0)
    {
        free( cmdPath );
        cmdPath = NULL;
    }

    /* Return the absolute command pathname. */
    return cmdPath;
}


static _TCHAR* classpath = "";


int main(int argc, char **argv) {
  printf("%s\n", findCommand("java"));
  puts(argv[0]);
}

